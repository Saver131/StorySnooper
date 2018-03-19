import urllib2
import json
import datetime
import csv
import time
import re

app_id = "1410060569295482"
app_secret = "cbad48dd385c8485034138b91f2fb8d1" # DO NOT SHARE WITH ANYONE!
url = "https://www.facebook.com/DramaAdd/posts/10154620035443291"
post_id = "212312312321"

access_token = app_id + "|" + app_secret

def request_until_succeed(url):
    req = urllib2.Request(url)
    success = False
    response = urllib2.urlopen(req)

    return response.read()

def processFacebookComment(comment, parent_id = ''):

    # The status is now a Python dictionary, so for top-level items,
    # we can simply call the key.

    # Additionally, some items may not always exist,
    # so must check for existence first

    comment_id = comment['id']
    comment_message = '' if 'message' not in comment else \
            unicode_normalize(comment['message'])
    comment_author = unicode_normalize(comment['from']['name'])
    comment_likes = 0 if 'like_count' not in comment else \
            comment['like_count']
    comment_count = 0 if 'comment_count' not in comment else comment['comment_count']

    if 'attachment' in comment:
        attach_tag = "[[%s]]" % comment['attachment']['type'].upper()
        comment_message = attach_tag if comment_message is '' else \
                (comment_message.decode("utf-8") + " " + \
                        attach_tag).encode("utf-8")

    # Time needs special care since a) it's in UTC and
    # b) it's not easy to use in statistical programs.

    comment_published = datetime.datetime.strptime(
            comment['created_time'],'%Y-%m-%dT%H:%M:%S+0000')
    comment_published = comment_published + datetime.timedelta(hours=-5) # EST
    comment_published = comment_published.strftime(
            '%Y-%m-%d %H:%M:%S') # best time format for spreadsheet programs

    # Return a tuple of all processed data

    return (comment_id, parent_id, comment_message, comment_author,
            comment_published, comment_likes,comment_count)
	
# Needed to write tricky unicode correctly to csv
def unicode_normalize(text):
    return text.translate({ 0x2018:0x27, 0x2019:0x27, 0x201C:0x22, 0x201D:0x22,
                            0xa0:0x20 }).encode('utf-8')

def scrapeFacebookPageFeedStatus(group_id,post_id,access_token):
    base = "https://graph.facebook.com/v2.7"
    node = "/"+group_id
    fields = "?fields=id,fan_count"
    parameters = "&access_token=%s&format=json" % access_token
    url = base + node + fields + parameters
    group = json.loads(request_until_succeed(url))
    
    node = "/"+group["id"]+"_"+post_id
    fields = "/?fields=message,link,created_time,type,name,id," + \
            "comments.limit(0).summary(true),shares,reactions" + \
            ".limit(0).summary(true)"
    parameters = "&access_token=%s" % access_token
    url = base+node+fields+parameters
    status = json.loads(request_until_succeed(url))
	
    #node = "/"+group["id"]+"_"+post_id+"/comments"
    #fields = "?fields=id,message,like_count,comment_count,created_time,comments,from,attachment"
    #parameters = "&order=chronological&limit=100&access_token=%s" % access_token
    #url = base + node + fields + parameters
    #comments = json.loads(request_until_succeed(url))
    
    #description = unicode_normalize(data["from"])
    #name = unicode_normalize(data["name"])
    #file = open("outtest.txt","w")
    #file.write(data)
    #file.write(name)
    #file.close()
    with open(group["id"]+'_'+post_id+'.txt', 'w') as file:
        json.dump(status,file)

#if __name__ == '__main__':
#    group = re.search('(?<=facebook\.com\/)([a-zA-Z0-9 ]*)',url)
#    post = re.search('(?<=posts\/)([0-9]*)',url)
#    scrapeFacebookPageFeedStatus(group.group(0),post.group(0),access_token)

base = "https://graph.facebook.com"
node = "/141108613290_10155114671233291"
fields = "?fields=comments.limit(0).summary(true),shares,reactions.limit(0).summary(true)"
parameters = "&access_token=%s" % access_token
url = base + node + fields + parameters
print url
group = json.loads(request_until_succeed(url))
 
print group

# The CSV can be opened in all major statistical programs. Have fun! :)
