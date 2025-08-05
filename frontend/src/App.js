import React, { useState, useEffect } from "react"
import "./OtherPages/style.css"
import Login from "./LoginSignin/Login.jsx"
import Register from "./LoginSignin/Register.jsx"
import HomePage from "./OtherPages/HomePage.jsx";
import Profile from "./OtherPages/Profile.jsx";
import useKrakenPrices from "./Cryptos/KrakenApi";

function App() {
  const [cryptos, setCryptos] = useState([]);
  const prices = useKrakenPrices(cryptos.map(c => c.symbol));
  const [currForm, setCurrForm] = useState(() => { return localStorage.getItem("currForm") || "Home"; });
  const [user, setUser] = useState(localStorage.getItem("user"));
  const toggle = (temp) => {
    setCurrForm(temp);
    localStorage.setItem("currForm", temp);
  }

  const handleLoginSuccess = (loggedUser) => {
    setUser(loggedUser);
    localStorage.setItem("user", JSON.stringify(loggedUser));
    toggle("Home");
  };

  useEffect(() => {
    const user = localStorage.getItem("user");
    if (user) {
      setUser(JSON.parse(user));
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("user");
    window.location.reload();
    setUser(null);
    toggle("Home");
  };

  return (
    <div>
      <header>
        <a onClick={() => toggle("Home")}>TradingSimulation</a>
        <nav>
          {user ? (<a onClick={() => toggle("Profile")}>Profile</a>) :
            (<a onClick={() => toggle("Login")}>Log In</a>)}
        </nav>
      </header>

      {currForm === "Home" && <HomePage />}
      {currForm === "Profile" && <Profile key={currForm} onLogout={handleLogout} />}

      {!user && currForm === "Login" &&
        (<Login onFormSwitch={toggle} onLoginSuccess={handleLoginSuccess} />)}

      {!user && currForm === "Register" && (<Register onFormSwitch={toggle} />)}
    </div>
  );
}

export default App;
