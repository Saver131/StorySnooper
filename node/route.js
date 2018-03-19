var todo = require('./todo')

module.exports={
	configure: function(app){
		app.get('/pagelist/',function(req,res){
			todo.get(res);
		});
	}
};