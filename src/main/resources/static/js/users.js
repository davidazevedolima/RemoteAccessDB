// Call the dataTables jQuery plugin
$(document).ready(function() {
  loadUsers();
  $('#users').DataTable();
  displayUsername();
});

function getHeaders(){
  return {
    'Accept' : 'application/json',
    'Content-Type' : 'application/json',
    'Authorization' : localStorage.token
  }
}

async function loadUsers() {
  const request = await fetch('api/users', {
    method: 'GET',
    headers: getHeaders()
  });
  const users = await request.json();

  let htmlUsers = '';
  for (let user of users){
    let deleteButton = '<a href="#" onclick="deleteUser(' + user.id + ')" class="btn btn-danger btn-circle"> <i class="fas fa-trash"></i></a>'
    let editButton = '<a href="#" class="btn btn-info btn-circle"><i class="fas fa-edit"></i></a>'

    let htmlUser = '<tr><td>'+ user.id
                    + '</td><td>'+ user.username
                    +'</td><td>'+ user.name +'</td><td>'
                    + user.lastName +'</td><td>'
                    + editButton + '<b>          </b>' + deleteButton + '  </td></tr>'  // TODO: Solve problem with space between icons
    htmlUsers += htmlUser;
  }



  document.querySelector('#users tbody').outerHTML = htmlUsers;
}


async function deleteUser(id) {
  if(!confirm("Do you want to delete this user?")){
    return
  }

  await fetch('api/users/' + id, {
    method: 'DELETE',
    headers: getHeaders()
  });

  location.reload();
}

function displayUsername(){
  document.getElementById("loggedUsername").outerHTML = localStorage.username;
}