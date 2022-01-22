$(document).ready(function() {
  loadText();
  displayUsername()
});

function loadText() {
  document.getElementById("fileTitle").outerHTML = "File title"
  //document.getElementById("richText").outerHTML = "Example example example example example example example example example example example example example example example example";
}

//TODO: create a common file to call this
function displayUsername(){
  document.getElementById("loggedUsername").outerHTML = localStorage.username;
}
