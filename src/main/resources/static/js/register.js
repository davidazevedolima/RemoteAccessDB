$(document).ready(function() {});

async function registerUser() {
    let userInfo = {};
    userInfo.name = document.getElementById('inputName').value;
    userInfo.lastName = document.getElementById('inputLastName').value;
    userInfo.username = document.getElementById('inputUsername').value;
    userInfo.password = document.getElementById('inputPassword').value
    let repeatPassword = document.getElementById('inputRepeatPassword').value;
    if (userInfo.password !== repeatPassword) {
        alert("Passwords do not match.");
         return;
    }

    const request = await fetch('api/users', {
        method: 'POST',
        headers: {
            'Accept' : 'application/json',
            'Content-Type' : 'application/json'
        },
        body: JSON.stringify(userInfo)
    });
    //TODO: check if the user is already registered (more or less how we check if the password is correct in the login)
    window.location.href = "index.html"
}
