$(document).ready(function() {});

async function loginUser() {
    let userInfo = {};
    userInfo.username = document.getElementById('inputUsername').value;
    userInfo.password = document.getElementById('inputPassword').value

    const request = await fetch('api/login', {
        method: 'POST',
        headers: {
            'Accept' : 'application/json',
            'Content-Type' : 'application/json'
        },
        body: JSON.stringify(userInfo)
    });
    const response = await request.text();
    //TODO: search about how to handle possible authentication errors
    if(response !== 'FAIL'){
        localStorage.token = response;
        localStorage.username = userInfo.username;
        window.location.href = 'users.html'
    } else {
        alert('Incorrect username or password, please try again.')
    }
}
