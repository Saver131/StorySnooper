var express = require('express');
var bodyparser = require('body-parser');
var fs = require("fs");


var veracity = {"value":74.22};

var test = 40.35;

var id = 2;
var app = express();
app.use(bodyparser.urlencoded({extended:true}));
app.use(bodyparser.json());

var pythonShell = require('python-shell');

var connection=require('./connection');

connection.init();

app.get('/pagelist/',function(req,res){
		connection.acquire(function(err,con){
			con.query('select * from page', function(err,result){
				con.release();
				res.send(result);
			});
		
		});
})

app.get('/pagetypelist/',function(req,res){
		connection.acquire(function(err,con){
			con.query('select distinct type from page', function(err,result){
				con.release();
				res.send(result);
			});
		
		});
})

app.get('/trendingtype/',function(req,res){
	var type = req.query.type;
		connection.acquire(function(err,con){
			con.query("select post.*,page.name FROM post INNER JOIN page ON post.page_id = page.id where page.type = '"+ type + "' order by growth DESC LIMIT 40", function(err,result){
				con.release();
				res.send(result);
			});
		
		});
})

app.get('/trending/',function(req,res){
	var all_id = req.query.id;
	if(!all_id){
		connection.acquire(function(err,con){
			con.query('select post.*,page.name FROM post INNER JOIN page ON post.page_id  = page.id order by growth DESC LIMIT 40', function(err,result){
				con.release();
				res.send(result);
			});
		});
	}
	else{
	if(Array.isArray(all_id)){
		var query = 'page_id = '+all_id[0];
		for(var i = 1; i < all_id.length;i++){
			query = query+' or page_id = '+ all_id[i];
		}
	}
	else{
		var query = 'page_id = ' + all_id;
	}
		connection.acquire(function(err,con){
			con.query('select post.*,page.name FROM post INNER JOIN page ON post.page_id = page.id where ' + query + ' order by growth DESC LIMIT 40', function(err,result){
				con.release();
				res.send(result);
			});
		
		});
	}
})

app.get('/new/',function(req,res){
	var all_id = req.query.id;
	if(!all_id){
		connection.acquire(function(err,con){
			con.query('select post.*,page.name FROM post INNER JOIN page ON post.page_id  = page.id order by date DESC LIMIT 40', function(err,result){
				con.release();
				res.send(result);
			});
		});
	}
	else{
	if(Array.isArray(all_id)){
		var query = 'page_id = '+all_id[0];
		for(var i = 1; i < all_id.length;i++){
			query = query+' or page_id = '+ all_id[i];
		}
	}
	else{
		var query = 'page_id = ' + all_id;
	}
		connection.acquire(function(err,con){
			con.query('select post.*,page.name FROM post INNER JOIN page ON post.page_id  = page.id where ' + query + '  order by date DESC LIMIT 40', function(err,result){
				con.release();
				res.send(result);
			});
		});
	}
})

app.get('/set',function(req,res){
	var all_id = req.query.id;
	if(Array.isArray(all_id)){
		var query = all_id[0];
		for(var i = 1; i < all_id.length;i++){
			query = query+','+ all_id[i];
		}
	}
	else{
		var query = all_id;
	}
		connection.acquire(function(err,con){
			con.query('update page set checked = case when id in (' + query + ')  then 1 else 0 END', function(err,result){
				con.release();
				res.sendStatus(200);
			});
		});
})

app.get('/veracity', function (req, res) {
   //res.end( "Truthness\n"+parseFloat(((Math.random()*1000)%30)+60).toFixed(2)+"%")
   var url = req.query.url;
	var options = {mode:'text',args:[url]};
	pythonShell.run('veracitytest.py',options,function(err,results){
	   if(err) throw err;
	   console.log(results);
	   res.end(String(results));
   });
})

//not used anymore
app.get('/users',function(req,res){
	var user_id = req.query.id;
	var auth = req.query.auth;
	
	fs.writeFile(__dirname+"/"+"testup.txt","user id:"+user_id+"\nauth:"+auth,function(err,data){
		if(err){
			return console(err);
		}
	});
	console.log("file was saved!");
	res.end("Done");
})

var server = app.listen(8000, function(){
	console.log('Server listening on port' + server.address().port);
})