import React, { useState } from "react";
import "./form.css"

const Login = (props) => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loginSucces, setLoginSucces] = useState("Success");

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/api/user/login",
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, password })
                });

            if (!response.ok) {
                setLoginSucces("Fail");
                return;
            }

            const user = await response.json();
            if (user) {
                localStorage.setItem("user", JSON.stringify(user));
                setLoginSucces("Success");
                props.onLoginSuccess(user);
                console.log("Logged in:", user);
            } else {
                setLoginSucces("Fail");
            }
        } catch (error) {
            console.error("Error:", error);
            alert("Something went wrong");
        }
    }

    return (
        <div className="inputDiv">
            <form className="inputForm" onSubmit={handleSubmit}>
                <h2>Log In</h2>

                <p className="registerText">
                    Don't have an account?
                    <a onClick={() => props.onFormSwitch("Register")}>Sign in</a>
                </p>
                <label htmlFor="email">E-mail</label>
                <input value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    type="email"
                    id="email"
                    name="email"
                    placeholder="Enter your e-mail" required />
                <label htmlFor="password">Password</label>
                <input value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    type="password"
                    id="password"
                    name="password"
                    placeholder="Enter your password" required />

                {loginSucces === "Success" ? <p></p> : <p>Wrong e-mail or password</p>}
                <button type="submit">Log In</button>
            </form>
        </div>
    )
}
export default Login;
