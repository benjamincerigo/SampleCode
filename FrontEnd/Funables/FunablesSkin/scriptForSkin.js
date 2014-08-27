//This is the java scripte for the change of the links to the Version One
//Date: Wed 27 Nov 2013 15:50:33 CET
//Version:1
var menu=document.getElementsByClassName("myshp_menu_side_categories ");

var divOne=menu.item(0);

var span = divOne.getElementsByClassName("myshp_box_title").item(0);

var textOne, newDiv, newLink, newSpan, newNode, newList;

var listOfCat, firstElement, mixMatchLink, listItems;

var whatIsIt, listOfliSub, lengthOfListHere;

textOne = span.innerHTML;
console.log("text one:" +textOne);

if(textOne == "Product"){

	span.style.visibility =  "hidden";

	newList = document.createElement("li");
	newList.class = "myshp_menu_side_item";
	newDiv = document.createElement("div");
	newDiv.class = "myshp_not_active_menu_item";
	newLink = document.createElement("a");
	newLink.href = "/test";
	newNode = document.createTextNode("Mix & Match");

	newLink.appendChild(newNode);
	newDiv.appendChild(newLink);
	newList.appendChild(newDiv);

	listOfCat = divOne.getElementsByClassName("myshp_menu_side_categories_1").item(0);

	firstElement = listOfCat.getElementsByClassName("myshp_menu_side_item").item(0);

	firstElement.style.border="0";
	whatIsIt = firstElement.getElementsByTagName("a").item(0);
	listOfliSub = firstElement.getElementsByTagName("div");

	
	lengthOfListHere = listOfliSub.length;

	if(location.href == "http://www.gloveables.nl/mixmatch"){
				

				for (var i=0;i<=lengthOfListHere;i++){
					

					if(i== 0)
					{
						listOfliSub.item(i).className="myshp_active_menu_item";
					}
					else{

					if ( (i % 2) == 0)
						{
						   
						    listOfliSub.item(i).className="myshp_active_menu_item";

						}
						else
						{
						  
						    listOfliSub.item(i).style.display="block";

						}
					}
				}
			}
	

	whatIsIt.href = "http://www.gloveables.nl/mixmatch";

}




