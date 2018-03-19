import urllib2
import json
import datetime
import csv
import time
import re

app_id = "1410060569295482"
app_secret = "cbad48dd385c8485034138b91f2fb8d1" # DO NOT SHARE WITH ANYONE!
url = "https://www.facebook.com/ThairathFan/posts/10155401529987439"

access_token = app_id + "|" + app_secret

def request_until_succeed(url):
	req = urllib2.Request(url)
	success = False
	while success is False:
		try: 
			response = urllib2.urlopen(req)
			if response.getcode() == 200:
				success = True
		except Exception, e:
			print e
			time.sleep(5)
			print "Error for URL %s: %s" % (url, datetime.datetime.now())
			print "Retrying."
	
	return response.read()
	
def processFacebookComment(comment, parent_id = ''):
	comment_id = comment['id']
	comment_message = '' if 'message' not in comment else \
		unicode_normalize(comment['message'])
	comment_likes = 0 if 'like_count' not in comment else \
		comment['like_count']
	comment_count = 0 if 'comment_count' not in comment else comment['comment_count']
	
	return (comment_id,parent_id,comment_message,comment_likes,comment_count)
	
def unicode_normalize(text):
	return text.translate({ 0x2018:0x27, 0x2019:0x27, 0x201C:0x22, 0x201D:0x22, 0xa0:0x20 }).encode('utf-8')
	
def getCommentFeed(post_id,access_token,num_comments):
	base = "https://graph.facebook.com"
	node = "/"+post_id+"/comments"
	fields = "?fields=id,parent,message,like_count,comment_count"
	parameters = "&order=chronological&limit=%s&access_token=%s" % (num_comments,access_token)
	url = base + node +fields + parameters
	
	data = request_until_succeed(url)
	
	if data is None:
		return None
	else:
		return json.loads(data)	
	
def scrapeFacebookPageFeedStatus(group_id,post_id,access_token):
	base = "https://graph.facebook.com/v2.7"
	node = "/"+group_id
	fields = "?fields=id,fan_count"
	parameters = "&order=chronological&access_token=%s" % access_token
	url = base + node + fields + parameters
	group = json.loads(request_until_succeed(url))
	
	node = "/"+group["id"]+"_"+post_id
	fields = "/?fields=created_time,type,id,message,name,description," + "shares,reactions" + ".limit(0).summary(true),comments.limit(0).summary(true),picture"
	parameters = "&access_token=%s&format=json" % access_token
	url = base + node + fields + parameters
	status = json.loads(request_until_succeed(url))
	
	with open(group["id"]+'_'+post_id+'.txt', 'w') as file:
		json.dump(status,file)

	num_processed = 0
	scrape_starttime = datetime.datetime.now()	
	
	with open(group["id"]+'_'+post_id+'_comment.csv', 'w') as file:
		w = csv.writer(file)
		w.writerow(["comment_id","parent_id","comment_message","comment_likes","comment_count"])
		has_next_page = True
		
		comments = getCommentFeed(group["id"]+"_"+post_id,access_token,100)
		
		while has_next_page and comments is not None:
			for comment in comments['data']:
				w.writerow(processFacebookComment(comment))
				#insert subcomment
				if comment["comment_count"] > 0:
				
					has_next_subpage = True
					
					subcomments = getCommentFeed(comment['id'],access_token,100)
						
					while has_next_subpage:
						for subcomment in subcomments['data']:
							w.writerow(processFacebookComment(subcomment,comment['id']))
							num_processed +=1
							if num_processed % 1000 == 0:
								print "%s Comments Processed: %s" % (num_processed,datetime,datetime,now())
							if 'paging' in subcomments:
								if ' next' in subcomments['paging']:
									subcomments = json.loads(request_until_succeed(subcomments['paging']['next']))
								else:
									has_next_subpage = False
							else:
								has_next_subpage = False
				
				num_processed += 1
				if num_processed %1000 ==0:
					print "%s Comments Processed: %s" % (numprocessed,datetime,datetime.now())
			
			if 'paging' in comments:
				if 'next' in comments['paging']:
					comments = json.loads(request_until_succeed(comments['paging']['next']))
				else:
					has_next_page = False
			else:
				has_next_page = False
				
		print "\nDone!\n%s Comments Processed in %s" % (num_processed,datetime.datetime.now() - scrape_starttime)
		
if __name__ == '__main__':
	if 'post' in url:
		group = re.search('(?<=facebook\.com\/)([a-zA-Z0-9 ]*)',url)
		post = re.search('(?<=posts\/)([0-9]*)',url)
		scrapeFacebookPageFeedStatus(group.group(0),post.group(0),access_token)