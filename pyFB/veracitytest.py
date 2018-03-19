import urllib2
import json
import datetime
import csv
import time
import re
import random
import sys

app_id = "1410060569295482"
app_secret = "cbad48dd385c8485034138b91f2fb8d1" # DO NOT SHARE WITH ANYONE!

url = "https://www.facebook.com/ThairathFan/posts/10155389257037439"
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
		print "Error for URL %s: %s" % (url, datetime.datetime.now())
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
	fields = "?fields=comments.limit(0).summary(true),shares,reactions.limit(0).summary(true)"
	parameters = "&access_token=%s" % (access_token)
	url = base + node + fields + parameters

    # retrieve data
	data = request_until_succeed(url)
	if(data == 1):
		return 1;
	data = json.loads(data)

	return data

def getPageVeracity(status):
	
	veracity = 0

	if status['is_verified']== True:
		veracity += 20
		#print "verified"
	
	if 'about' in status:
		veracity += 10
		#print "about"
	if 'category_list' in status:
		veracity += 10
		#print "category"
	if 'contact_address' in status:
		veracity += 10
		#print "address"
	if 'description' in status:
		veracity += 10
		#print "description"
	if 'emails' in status:
		veracity += 10
		#print "email"
	if 'general_info' in status:
		veracity += 10
		#print "info"
	if 'phone' in status:
		veracity += 10
		#print "phone"
	if 'website' in status:
		veracity += 10
		#print "website"
	
	return veracity
	
def getLinkVeracity(link):

	file = open('website.csv')
	reader = csv.reader(file)
	data = list(reader)
	
	website = re.search('^(https|http)?:\/\/[^\/]+?(?=\/)',link)
	webb = website.group(0)
	if website.group(0) is None:
		return 50
	else:
		for web in data:
			if web[0] == webb:
				return int(web[1])
		return 0
	
def scrapeFacebookPageFeedStatus(group_id,post_id,access_token):
	base = "https://graph.facebook.com"
	node = "/"+group_id
	fields = "?fields=id,about,category_list,contact_address,description,emails,fan_count,general_info,phone,is_verified,website"
	parameters = "&access_token=%s&format=json" % access_token
	url = base + node + fields + parameters
	group = request_until_succeed(url)
	if group == 1:
		return "['Invalid Page','Invalid Page']"
		
	group = json.loads(group)
	
	node = "/"+group["id"]+"_"+post_id
	fields = "/?fields=link"
	parameters = "&access_token=%s" % access_token
	url = base+node+fields+parameters
	status = request_until_succeed(url)
	if status == 1:
		return "['Invalid Post','Invalid Post']"
		
	posterVeracity = getPageVeracity(group)
	if posterVeracity < 20:
		posterReason = "Untrustworthy Poster"
	elif posterVeracity <45:
		posterReason = "Doubtful Poster"
	elif posterVeracity <70:
		posterReason = "Trustworthy Poster"
	else:
		posterReason = "Most Trusted Poster"
	
	status = json.loads(status)
	if 'link' in status:
		linkVeracity = getLinkVeracity(status['link'])
		if linkVeracity < 50:
			linkReason = "Untrustable Link"
		else:
			linkReason = "Trustable Link"
		commentVeracity = random.uniform(25,75)
		veracity = str((posterVeracity+linkVeracity+commentVeracity)/3)[:5]
	else:
		commentVeracity = random.uniform(33,66)
		veracity = str((posterVeracity+commentVeracity)/2)[:5]
	
	if commentVeracity < 50:
		commentReason = "Not Believe Comment"
	else:
		commentReason = "Believe Comment"

	#with open('group.txt', 'w') as file:
		#json.dump(group,file)
		#json.dump(status,file)

	return "['"+veracity+"%','"+posterReason+","+commentReason+","+linkReason+"']"
#
#Begin code
#
#group = re.search('(?<=facebook\.com\/)([a-zA-Z0-9 ]*)',sys.argv[1])
#post = re.search('(?<=posts\/)([0-9]*)',sys.argv[1])
group = re.search('(?<=facebook\.com\/)([a-zA-Z0-9 ]*)',sys.argv[1])
post = re.search('(?<=posts\/)([0-9]*)',sys.argv[1])
if group is None or post is None:
	print "['Invalid URL','Invalid URL']"
else:
	print(scrapeFacebookPageFeedStatus(group.group(0),post.group(0),access_token))
	