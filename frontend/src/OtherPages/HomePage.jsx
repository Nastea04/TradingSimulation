import CryptoTable from "./CryptoTable.jsx";
const HomePage = (prices) => {

    return (
        <div>
            <div className="homeQuote">
                <h1>Practice without risk.<br />Trade with confidence.</h1>
            </div>
            <CryptoTable />
        </div>
    );
};

export default HomePage;
