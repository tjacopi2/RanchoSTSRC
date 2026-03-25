var http = require('http'); // Import Node.js core module
var fs = require("fs");     // file module 
const urlModule = require('url');

var port = 5000;
var logDirectory = './';

function padWithLeadingZero(value) {
  return ('0' + value).slice(-2)
}

//function makeLogFileName(date) {
//  return logDirectory + 'log' + date.getFullYear() + '-' + padWithLeadingZero((date.getMonth() + 1)) + '-' + padWithLeadingZero(date.getDate()) + '.csv';
//}

function removeSpecialChar(str){
  if(str == null || str == ''){
     return '';
  }
  // Remove any quotes or commas from the text
   return str.replace(/[,"]/g, '');
}

// Write JSON map
function writeMap(fileName, map) {
  var jsonObject = {};  
  map.forEach((value, key) => {  
    jsonObject[key] = value  
  });  
  var data = JSON.stringify(jsonObject, null, " ");  
  fs.writeFileSync(fileName, data);
}

// Read JSON map
function readMap(fileName) {
  const rawdata = fs.readFileSync(fileName);
  var jsonObject2 = JSON.parse(rawdata);
  var guestMap2 = new Map();
  for (var value in jsonObject2) {  
    guestMap2.set(value,jsonObject2[value])  
  }
  return guestMap2;  
}

function getKey(address, map) {
  // [Circle, Dr., #206, #108, Road, Lane, Way, Dr, Court, #3, Ct, Ave, Drive, Street, Brookmere, Ct.]
  const keySuffixes = ["", " Drive", " Way", " Avenue", " Street", " Court", " Ct", " Ct."];
  for (var suffix in keySuffixes) {
    var key = address + keySuffixes[suffix];
	if (map.has(key)) {
	  return key;
	}
  }
  
  return null;
}

function writeLogRecord(address, person, count) {
  
  if (count == 0) {
    return;
  }
  
  // Create log record
  const current = new Date();
  const cDate = current.getFullYear() + '-' + padWithLeadingZero((current.getMonth() + 1)) + '-' + padWithLeadingZero(current.getDate());
  const cTime = current.getHours() + ":" + padWithLeadingZero(current.getMinutes()) + ":" + padWithLeadingZero(current.getSeconds());
  const dateTime = cDate + ' ' + cTime;
  const singleLogEntry = '"'+ dateTime +'","' + removeSpecialChar(address) +'","' + removeSpecialChar(person) + '"\n';
  
  var logEntry = "";
  for (i=0; i<count; i++) {
    logEntry = logEntry + singleLogEntry;
  }
  
  // Just use one log file, they are not that large and its just easier to manage.
  //if (logDate != current.getDate() ) {
  //  logFileName =  makeLogFileName(current);
  //  logDate = current.getDate();
  //}
  
  // write log record
  fs.appendFile(logFileName, logEntry, err => {
       if (err) {
         console.error(err)
         return
       }
   });

}

if (process.argv.length >= 3) {
  logDirectory = process.argv[2];
  if (!logDirectory.endsWith('\\')) {
    logDirectory = logDirectory + '\\';
  }
}  

if (process.argv.length >= 4) {
  port = process.argv[3];
} 

console.log('Listening on port ' + port + '.  Log files will be written to ' + logDirectory);

var logFileName =  logDirectory + 'entryLog.csv';
//const startupTime = new Date();
//var logFileName =  makeLogFileName(startupTime);
//var logDate = startupTime.getDate();


//console.log(removeSpecialChar('0123456789asdfgghfleoiWOERIWKVDSLDFDJK,.;:",'));   // Test to insure commas and quotes are removed

// start http server to write log entries
var server = http.createServer(function (req, res) {   //create web server
    //console.log(req);
	console.log("url - " + req.url);
	//console.log("method - " + req.method);
	res.setHeader('Access-Control-Allow-Origin','*');      // Need to do this so browser does not complain about CORS policy
	const urlComponents = req.url.split('/');
	if (req.method == 'POST' && urlComponents.length == 4 && urlComponents[1] == "log") {
	    //console.log('in post block');
		const address = decodeURIComponent(urlComponents[2]); 
		const person = decodeURIComponent(urlComponents[3]); 
		writeLogRecord(address, person, 1);
		
		res.statusCode = 200;
		res.end();
	} else if (req.method == 'GET' && urlComponents.length == 3 && urlComponents[1] == "getGuestPassCount") {
		const requestedAddress = decodeURIComponent(urlComponents[2]); 
		//console.log("getting guestPassCount for address " + requestedAddress);
		const map = readMap("guestPasses.json");
		//console.log(map);
		const key = getKey(requestedAddress, map);
		var guestPasses = 0;
		if (key != null) {
		  guestPasses = map.get(key);
		}
		  
		res.writeHead(200, {'Content-Type': 'text/plain'});	
        res.write(guestPasses.toString());	
		res.end();
	} else if (req.method == 'GET' && urlComponents.length == 2 && urlComponents[1].startsWith("addGuests?")) {
	    const queryObject = urlModule.parse(req.url,true).query;
		const requestedAddress = queryObject.address;
		var grandkidCount = 0;
		var freeGuestPasses = 0;
		var paidGuestPasses = 0;
		//console.log("query object = " + JSON.stringify(queryObject));
		if (queryObject.grandkids != "") {
		  grandkidCount = parseInt(queryObject.grandkids);
		  if (grandkidCount < 0) {
		    grandkidCount = 0;
		  }	
        }
		var guestCount = 0;
		if (queryObject.count != "") {
		  guestCount = parseInt(queryObject.count);
		  if (guestCount < 0) {
		    guestCount = 0;
		  }	
        }
		var useFreePasses = true;
		if (queryObject.nofreepass == "on") {
		  useFreePasses = false;
        }
		console.log("Adding " + guestCount + " guests and " + grandkidCount + " grandkids for address " + requestedAddress + " useFreePass=" + useFreePasses);
		const map = readMap("guestPasses.json");
		const key = getKey(requestedAddress, map);
		if (key != null && useFreePasses) {
		  const newGuestPassCount = map.get(key) - guestCount;
		  if (newGuestPassCount > 0) {
		    map.set(key, newGuestPassCount);
			freeGuestPasses = guestCount;
		  } else {
		    freeGuestPasses = map.get(key);          
			paidGuestPasses = guestCount - freeGuestPasses;
			map.set(key, 0);        // Leave the entries in place when the count goes to zero
		    //map.delete(key);
		  }
		  writeMap("guestPasses.json", map);
		} else {
		  paidGuestPasses = guestCount;
		}
		  
		// Log guests
		writeLogRecord(requestedAddress, "Guest_Paid", paidGuestPasses);
		writeLogRecord(requestedAddress, "Guest_FreePass", freeGuestPasses);
		writeLogRecord(requestedAddress, "Guest_Grandkid", grandkidCount);
		
		res.writeHead(200, {'Content-Type': 'text/plain'});	
		res.write("Success");
		res.end();
    } else
      res.end('Invalid Request!!');

});

server.listen(port); 

console.log('Node.js web server at port ' + port + ' is running..')