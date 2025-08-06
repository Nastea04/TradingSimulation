import React, { useState } from "react";
import "./form.css"

const Register = (props) => {
    const [fname, setFname] = useState("");
    const [lname, setLname] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [registerSucces, setRegisterSucces] = useState("Success");

    const handleSubmit = async (e) => {
        e.preventDefault();
        const name = fname + " " + lname;
        try {
            const response = await fetch("http://localhost:8080/api/user/register",
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ name, email, password })
                });

            const answer = await response.text();
            if (answer === "Registration successful") {
                alert(answer);
                props.onFormSwitch("Login");
                setRegisterSucces("Success");
            }
            else {
                setRegisterSucces("Fail");
            }
        } catch (error) {
            console.error("Error:", error);
            alert("Something went wrong");
        }
    }
    return (
        <div className="inputDiv">
            <form className="inputForm" onSubmit={handleSubmit}>
                <h2>Create your account</h2>
                <p className="registerText">
                    Already have an account?
                    <a onClick={() => props.onFormSwitch("Login")}>Log in</a>
                </p>

                <label htmlFor="fname">First name</label>
                <input value={fname}
                    onChange={(e) => setFname(e.target.value)}
                    type="text"
                    id="fname"
                    name="fname"
                    placeholder="Enter your first name" required />

                <label htmlFor="lname">Last name</label>
                <input value={lname}
                    onChange={(e) => setLname(e.target.value)}
                    type="text"
                    id="lname"
                    name="lname"
                    placeholder="Enter your last name" required />

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
                {
                    registerSucces === "Success" ? <p></p> : <p>There is an existing account with that e-mail</p>
                }
                <button type="submit">Create account</button>
            </form>
        </div>
    )
}

export default Register