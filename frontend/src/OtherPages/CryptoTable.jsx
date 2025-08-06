import { useEffect, useState } from "react";
import useKrakenPrices from "../Cryptos/KrakenApi";
import "./styleTable.css";
import "./formSellBuy.css";

const CryptoTable = () => {
    const [cryptos, setCryptos] = useState([]);
    const [isBuyOpen, setIsBuyOpen] = useState(false);
    const [user, setUser] = useState(JSON.parse(localStorage.getItem("user")));
    const [balance, setBalance] = useState(user?.balance || 0);
    const [selectedCrypto, setSelectedCrypto] = useState(null);
    const [buyQuantity, setBuyQuantity] = useState("");
    const [totalBuy, setTotalBuy] = useState(0);

    const prices = useKrakenPrices(cryptos.map(c => c.symbol));

    const openBuyForm = (crypto) => {
        setSelectedCrypto(crypto);
        setIsBuyOpen(true);
        setBuyQuantity("");
        setTotalBuy(0);
    };

    const closeBuyForm = () => {
        setSelectedCrypto(null);
        setIsBuyOpen(false);
        setBuyQuantity("");
        setTotalBuy(0);
    };

    const fetchCryptos = async () => {
        try {
            const res = await fetch("http://localhost:8080/api/cryptos");
            if (!res.ok) throw new Error("Failed to fetch cryptos");
            const data = await res.json();
            setCryptos(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error("Error fetching cryptos:", err);
        }
    };

    const fetchUserData = async (userId) => {
        try {
            const res = await fetch(`http://localhost:8080/api/user/get/${userId}`);
            if (!res.ok) throw new Error("Failed to fetch user");
            const data = await res.json();
            setUser(data);
            setBalance(data.balance);
            localStorage.setItem("user", JSON.stringify(data));
        } catch (err) {
            console.error("Error fetching user data:", err);
        }
    };



    useEffect(() => {
        fetchCryptos();
        const storedUser = JSON.parse(localStorage.getItem("user"));
        if (storedUser?.id) {
            fetchUserData(storedUser.id);
        }
    }, []);

    const makeBuy = () => {
        const qty = parseFloat(buyQuantity);
        if (isNaN(qty) || qty <= 0) {
            alert("Enter quantity");
            return;
        }

        const price = prices[selectedCrypto.symbol];
        if (!price) {
            alert("No price available! Try again later.");
            return;
        }

        const symbol = encodeURIComponent(selectedCrypto.symbol);
        const name = encodeURIComponent(selectedCrypto.name);

        fetch(`http://localhost:8080/api/trade/buy/${user.id}?cryptoSymbol=${symbol}&quantity=${qty}&price=${price}&cryptoName=${name}`, {
            method: "POST"
        })
            .then(async (res) => {
                const message = await res.text();
                if (!res.ok) throw new Error(message);
                return message;
            })
            .then((msg) => {
                alert(msg);
                fetchUserData();
                closeBuyForm();
            })
            .catch((err) => {
                alert(err.message);
            });
    };

    return (
        <div>
            <table>
                <thead>
                    <tr>
                        <th className="id">#</th>
                        <th className="logo">Logo</th>
                        <th className="symbol">Symbol</th>
                        <th className="name">Name</th>
                        <th className="price">Price</th>
                        <th className="buy"></th>
                    </tr>
                </thead>
                <tbody>
                    {cryptos.map(coin => (
                        <tr key={coin.symbol}>
                            <td className="id">{coin.id}</td>
                            <td className="logo"><img src={coin.url} alt={coin.name} /></td>
                            <td className="symbol">{coin.symbol}</td>
                            <td className="name">{coin.name}</td>
                            <td className="price">
                                {prices[coin.symbol] ? `$${prices[coin.symbol]}` : "Loading..."}
                            </td>
                            {user && (
                                <td className="buy">
                                    <button onClick={() => openBuyForm(coin)}>Buy</button>
                                </td>
                            )}
                        </tr>
                    ))}
                </tbody>
            </table>

            {isBuyOpen && selectedCrypto && (
                <div className="overlay">
                    <div className="form-container">
                        <h1>Buy {selectedCrypto.name}</h1>
                        <p>Symbol: {selectedCrypto.symbol}</p>
                        <p>Balance: ${balance.toFixed(2)}</p>
                        <p>Price: {prices[selectedCrypto.symbol] ? `$${prices[selectedCrypto.symbol]}` : "Loading..."}</p>
                        <p>Total: ${totalBuy.toFixed(2)}</p>
                        <input
                            type="number"
                            min="0"
                            step="0.01"
                            placeholder="Quantity to buy"
                            value={buyQuantity}
                            onChange={(e) => {
                                const value = e.target.value;
                                setBuyQuantity(value);

                                const qty = parseFloat(value);
                                const price = prices[selectedCrypto.symbol];
                                if (!isNaN(qty) && qty > 0 && price) {
                                    setTotalBuy(qty * price);
                                } else {
                                    setTotalBuy(0);
                                }
                            }}
                        />
                        <div className="buttons">
                            <button className="confirm" onClick={makeBuy}>Confirm</button>
                            <button className="cancel" onClick={closeBuyForm}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default CryptoTable;
