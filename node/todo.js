var connection = require('./connection');

function Todo(){
	this.get = function(res){
		connection.acquire(function(err,con){
			con.query('select * from page', function(err,result){
				con.release();
				res.send(result);
			});
		});
	};
}
module.exports = new Todo();