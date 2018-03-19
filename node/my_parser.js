var fs = require('fs');
fs.readFile('log.txt',function(err,logData){
	// if error, throw it to display exeception and end app
	if (err) throw err;
	
	//logData is buffer, convert it to string
	var text = logData.toString();
	
	var results = {};
	
	//Break up file into lines
	var lines = text.split('\n');
	
	lines.forEach(function(line){
		var parts = line.split(' ');
		var letter = parts[1];
		var count = parseInt(parts[2]);
		
		if(!results[letter]){
			results[letter]=0;
		}
		
		results[letter]+=parseInt(count);
	});
	console.log(results);
});