import urllib2
import json
import datetime
import csv
import time
import re
import MySQLdb
import random

app_id = "1410060569295482"
app_secret = "cbad48dd385c8485034138b91f2fb8d1" # DO NOT SHARE WITH ANYONE!

access_token = app_id + "|" + app_secret


def request_until_succeed(url):
	req = urllib2.Request(url)
	try: 
		response = urllib2.urlopen(req)
		if response.getcode() == 200:
			success = True
		return response.read()
	except Exception, e:
		print e
		time.sleep(5)

		print "Error for URL %s: %s" % (url, datetime.datetime.now())
		print "Retrying."
		return 1

# Needed to write tricky unicode correctly to csv
def unicode_normalize(text):
	return text.translate({ 0x2018:0x27, 0x2019:0x27, 0x201C:0x22, 0x201D:0x22,
                            0xa0:0x20 }).encode('utf-8')

def getFacebookPageFeedData(post_id, access_token):

    # Construct the URL string; see http://stackoverflow.com/a/37239851 for
    # Reactions parameters
	base = "https://graph.facebook.com"
	node = "/%s" % post_id 
	fields = "?fields=message,link,created_time,comments.limit(0).summary(true),shares,reactions.limit(0).summary(true)"
	parameters = "&access_token=%s" % (access_token)
	url = base + node + fields + parameters

    # retrieve data
	data = request_until_succeed(url)
	if(data == 1):
		return 1;
	data = json.loads(data)

	return data
	
def processFacebookPageFeedStatus(status):

    # The status is now a Python dictionary, so for top-level items,
    # we can simply call the key.

    # Nested items require chaining dictionary keys.

	num_reactions = 0 if 'reactions' not in status else status['reactions']['summary']['total_count']
	num_comments = 0 if 'comments' not in status else status['comments']['summary']['total_count']
	num_shares = 0 if 'shares' not in status else status['shares']['count']

	status_message = '' if 'message' not in status.keys() else unicode_normalize(status['message'])	
	status_message = re.sub('"','""',status_message.rstrip())
	status_message = re.sub("'","''",status_message.rstrip())
	status_message = (status_message[:320] + '..') if len(status_message) > 321 else status_message
	status_link = 0 if 'link' not in status.keys() else 1
		
	post_published = datetime.datetime.strptime(status['created_time'],'%Y-%m-%dT%H:%M:%S+0000')
	post_published = post_published + datetime.timedelta(hours=-5) # EST
	post_published = post_published.strftime('%Y-%m-%d %H:%M:%S') # best time format for spreadsheet programs
    # Return a tuple of all processed data
	return '"'+status_message+'"'+','+str(num_reactions)+','+str(num_comments)+','+str(num_shares)+','+'"'+str(post_published)+'"'+','+str(status_link)
#
#Begin code
#
db = MySQLdb.connect("localhost","root","","storysnooper",charset='utf8')

# prepare a cursor object using cursor() method
cursor = db.cursor()

sql="select page_id,post_id,growth from cma where n=3"

try:
   # Execute the SQL command
	cursor.execute(sql)
   # Fetch all the rows in a list of lists.
	results = cursor.fetchall()

except:
	print "Error: unable to query data"

sql = "insert into post values "
for row in results:
	status = getFacebookPageFeedData(str(row[0])+"_"+str(row[1]),access_token)
	if(status == 1):
		sql_del = "delete from cma where page_id="+str(row[0])+" and post_id="+str(row[1])
		try:
			# Execute the SQL command
			cursor.execute(sql_del)
			# Commit your changes in the database
			db.commit()
			print"Delete Success"
		except:
			# Rollback in case there is any error
			db.rollback()
			print"Delete Fail"
	else:
		if(row[0]==141108613290):
			vera = str(random.uniform(65,85))[:5]
		elif(row[0]==146406732438):
			vera = str(random.uniform(80,95))[:5]
		elif(row[0]==129558990394402):
			vera = str(random.uniform(75,95))[:5]
		elif(row[0]==136045649773277):
			vera = str(random.uniform(60,80))[:5]
		elif(row[0]==159005400803388):
			vera = str(random.uniform(55,80))[:5]
		else:
			vera = str(random.uniform(35,60))[:5]
		sql += '('+str(row[0])+','+str(row[1])+','+processFacebookPageFeedStatus(status)+','+vera +','+str(row[2])+"),"

sql = sql[:-1]	
try:
   # Execute the SQL command
	cursor.execute(sql)
   # Fetch all the rows in a list of lists.
	db.commit()
	print "Success"
except:
	print "Fail"
# disconnect from server
db.close()