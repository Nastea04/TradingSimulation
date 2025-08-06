import React, { useState, useEffect } from "react";
import useKrakenPrices from "../Cryptos/KrakenApi";
import "./formSellBuy.css"
import "./style.css";

const Profile = ({ onLogout }) => {
    const [user, setUser] = useState(JSON.parse(localStorage.getItem("user")));
    const [balance, setBalance] = useState(user.balance);
    const [holdings, setHoldings] = useState([]);
    const [history, setHistory] = useState([]);
    const [profitLoss, setProfitLoss] = useState(0.0);
    const [isSellOpen, setIsSellOpen] = useState(false);
    const [selectedCrypto, setSelectedCrypto] = useState(null);
    const [sellQuantity, setSellQuantity] = useState("");
    const [totalSell, setTotalSell] = useState(0);

    const prices = useKrakenPrices(holdings.map(h => h.cryptoSymbol));

    const openSellForm = (crypto) => {
        setSelectedCrypto(crypto);
        setIsSellOpen(true);
    }
    const closeSellForm = () => {
        setSelectedCrypto(null);
        setIsSellOpen(false);
        setSellQuantity("");
        setTotalSell(0);
    }

    const fetchData = async (userId) => {
        try {
            const userRes = await fetch(`http://localhost:8080/api/user/get/${userId}`);
            const userData = await userRes.json();
            setUser(userData);
            setBalance(userData.balance);

            const holdingsRes = await fetch(`http://localhost:8080/api/user/holdings/${userId}`);
            const holdingsData = await holdingsRes.json();
            setHoldings(holdingsData);

            const historyRes = await fetch(`http://localhost:8080/api/user/history/${userId}`);
            const historyData = await historyRes.json();
            setHistory(historyData);

            const plRes = await fetch(`http://localhost:8080/api/user/profitloss/${userId}`, { method: "PUT" });
            const plData = await plRes.json();
            setProfitLoss(plData);
        } catch (err) {
            console.error("Error loading profile:", err);
        }
    };



    useEffect(() => {
        if (!user?.id) return;
        fetchData(user.id);
    }, []);


    const resetProfile = async () => {
        if (!user?.id) return;

        if (!window.confirm("Are you sure you want to reset your profile?")) return;

        try {
            const res = await fetch(`http://localhost:8080/api/user/reset/${user.id}`, {
                method: "PUT"
            });

            if (!res.ok) {
                throw new Error(await res.text());
            }

            alert("Profile reset successfully!");
            fetchData(user.id);
        } catch (err) {
            console.log(`Error resetting profile: ${err.message}`);
            alert("Error! Try again later.");
        }
    };




    const makeSell = () => {
        const qty = parseFloat(sellQuantity);

        if (isNaN(qty) || qty <= 0) {
            alert("Enter quantity");
            return;
        }

        const currentPrice = prices[selectedCrypto.cryptoSymbol];
        if (!currentPrice) {
            alert("No price available! Try again later.");
            return;
        }

        const symbol = encodeURIComponent(selectedCrypto.cryptoSymbol);
        fetch(`http://localhost:8080/api/trade/sell/${user.id}?cryptoSymbol=${symbol}&quantity=${qty}&price=${currentPrice}&cryptoName=${selectedCrypto.cryptoName}`, {
            method: "POST"
        })

            .then(async (res) => {
                const message = await res.text();
                if (!res.ok) {
                    throw new Error(message);
                }
                return message;
            })
            .then((msg) => {
                alert(msg);
                fetchData(user.id);
                closeSellForm();
            })
            .catch((err) => {
                alert(err.message);
            });
    };





    return (
        <div className="allProfileInfo">
            <div className="profile">
                <div className="profileInfo">
                    <h1>{user?.name}</h1>
                    <p>{user?.email}</p>
                </div>
                <div className="balance">
                    <h1>Balance: {typeof balance === "number" ? balance.toFixed(2) : "0.00"}$</h1>
                    <h2 className={profitLoss >= 0.0 ? "profit" : "loss"}>{profitLoss >= 0.0 ? "Profit: " : "Loss: "}{profitLoss.toFixed(2)}$</h2>
                </div>

                <div className="profileButtons">
                    <button onClick={resetProfile}>Reset</button>
                    <button onClick={onLogout}>Logout</button>
                </div>
            </div>
            <div className="profileInfo2">
                <div className="boxLists">
                    <div className="headerLists"><h1>Holdings</h1></div>
                    <div className="infoLists">
                        {holdings.length > 0 ?
                            <table>
                                <thead>
                                    <tr>
                                        <th className="symbol">Symbol</th>
                                        <th className="cryptoName">Name</th>
                                        <th className="quantity">Quantity</th>
                                        <th className="sell"></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {holdings.map((h, index) => (
                                        <tr key={index}>
                                            <td className="symbol">{h.cryptoSymbol}</td>
                                            <td className="cryptoName">{h.cryptoName}</td>
                                            <td className="quantity">
                                                {typeof h.quantity === "number" ? h.quantity.toFixed(2) : "0.00"}
                                            </td>

                                            <td className="sell"><button onClick={() => openSellForm(h)}>Sell</button></td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                            :
                            <p>No holdings yet</p>}
                    </div>
                </div>
                <div className="boxLists">
                    <div className="headerLists"><h1>History</h1></div>
                    <div className="infoLists">
                        {history.length > 0 ? (
                            <table>
                                <thead>
                                    <tr>
                                        <th>Symbol</th>
                                        <th>Name</th>
                                        <th>Type</th>
                                        <th>Quantity</th>
                                        <th>Time</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {history.map((h, index) => (
                                        <tr key={index}>
                                            <td className="symbol">{h.cryptoSymbol}</td>
                                            <td>{h.cryptoName}</td>
                                            <td>{h.type}</td>
                                            <td>{h.quantity}</td>
                                            <td className="time">{h.timePurchase}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        ) : (
                            <p>No transactions made yet</p>
                        )}
                    </div>
                </div>
                {isSellOpen === true ?

                    <div className="overlay">
                        <div className="form-container">
                            <h1>Sell {selectedCrypto.cryptoName}</h1>
                            <p>Symbol: {selectedCrypto.cryptoSymbol}</p>
                            <p>Available: {typeof selectedCrypto?.quantity === "number" ? selectedCrypto.quantity.toFixed(2) : "0.00"}</p>
                            <p>Price: {prices[selectedCrypto.cryptoSymbol]}</p>
                            <p>Total: {typeof totalSell === "number" ? totalSell.toFixed(2) : "0.00"}</p>

                            <input type="number" min="0" step="0.01" max={selectedCrypto.quantity}
                                placeholder="Quantity to sell" onChange={(e) => {
                                    const value = e.target.value;
                                    setSellQuantity(value);

                                    const qty = parseFloat(value);
                                    const price = prices[selectedCrypto.cryptoSymbol];
                                    if (!isNaN(qty) && qty > 0 && price) {
                                        setTotalSell(qty * price);
                                    } else {
                                        setTotalSell(0);
                                    }
                                }} />

                            <div className="buttons">
                                <button className="confirm" onClick={() => makeSell()}>Confirm</button>
                                <button className="cancel" onClick={() => closeSellForm()}>Cancel</button>
                            </div>
                        </div>
                    </div> : <></>};


            </div>

        </div>


    )
}

export default Profile