// Call the dataTables jQuery plugin
$(document).ready(function() {
  loadDocuments();
  displayUsername();
  $('#documents').DataTable();
});

function getHeaders(){
  return {
    'Accept' : 'application/json',
    'Content-Type' : 'application/json',
    'Authorization' : localStorage.token
  }
}

async function loadDocuments() {
  const request = await fetch('api/documents', {
    method: 'GET',
    headers: getHeaders()
  });
  const documents = await request.json();

  let htmlDocuments = '';
  for (let document of documents){
    let deleteButton = '<a href="#" onclick="deleteDocument(' + document.id + ')" class="btn btn-danger btn-circle"> <i class="fas fa-trash"></i></a>'
    let editButton = '<a href="editdocument.html" class="btn btn-info btn-circle"><i class="fas fa-edit"></i></a>'

    let htmlDocument = '<tr><td>'+ document.id
                    + '</td><td>'+ document.title
                    +'</td><td>'+ editButton + '<b>          </b>' + deleteButton + '  </td></tr>'  // TODO: Solve problem with space between icons
    htmlDocuments += htmlDocument;
  }



  document.querySelector('#documents tbody').outerHTML = htmlDocuments;
}

async function deleteDocument(id) {
  if(!confirm("Do you want to delete this document?")){
    return
  }

  await fetch('api/documents/' + id, {
    method: 'DELETE',
    headers: getHeaders()
  });

  location.reload();
}

//TODO: create a common file to call this
function displayUsername(){
  document.getElementById("loggedUsername").outerHTML = localStorage.username;
}