import urllib2
import json
import datetime
import csv
import time
import re
import MySQLdb

app_id = "1410060569295482"
app_secret = "cbad48dd385c8485034138b91f2fb8d1" # DO NOT SHARE WITH ANYONE!

access_token = app_id + "|" + app_secret

since = "1493612069"

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

# Needed to write tricky unicode correctly to csv
def unicode_normalize(text):
    return text.translate({ 0x2018:0x27, 0x2019:0x27, 0x201C:0x22, 0x201D:0x22,
                            0xa0:0x20 }).encode('utf-8')

def getFacebookPageFeedData(page_id, access_token, num_statuses):

    # Construct the URL string; see http://stackoverflow.com/a/37239851 for
    # Reactions parameters
    base = "https://graph.facebook.com"
    node = "/%s/posts" % page_id 
    fields = "?fields=id,comments.limit(0).summary(true),shares,reactions.limit(0).summary(true)"
    parameters = "&limit=%s&since=%s&access_token=%s" % (num_statuses,since, access_token)
    url = base + node + fields + parameters

    # retrieve data
    data = json.loads(request_until_succeed(url))
    return data


def processFacebookPageFeedStatus(status):

    # The status is now a Python dictionary, so for top-level items,
    # we can simply call the key.

    # Additionally, some items may not always exist,
    # so must check for existence first

    status_id = status['id'].split('_')
    page_id = status_id[0]
    post_id = status_id[1]

    # Nested items require chaining dictionary keys.

    num_reactions = 0 if 'reactions' not in status else status['reactions']['summary']['total_count']
    num_comments = 0 if 'comments' not in status else status['comments']['summary']['total_count']
    num_shares = 0 if 'shares' not in status else status['shares']['count']
    cma = num_reactions + num_comments + num_shares
    # Return a tuple of all processed data
    return str(page_id)+','+str(post_id)+','+str(cma)

#
#Begin code
#
db = MySQLdb.connect("localhost","root","","storysnooper")

# prepare a cursor object using cursor() method
cursor = db.cursor()

sql="select id from page where not id = 136045649773277"

try:
   # Execute the SQL command
    cursor.execute(sql)
   # Fetch all the rows in a list of lists.
    results = cursor.fetchall()
    sql = "insert into cma values "
    for row in results:
        page_id = row[0]
        # Call get post from page here
        has_next_page = True
        num_processed = 0   # keep a count on how many we've processed
        scrape_starttime = datetime.datetime.now()

        print "Scraping %s Facebook Page: %s\n" % (page_id, scrape_starttime)

        statuses = getFacebookPageFeedData(page_id, access_token, 100)
        if not statuses['data']:
            continue
        else:
            while has_next_page:
                for status in statuses['data']:

                # Ensure it is a status with the expected metadata
                    if 'reactions' in status:
                        sql += "("+processFacebookPageFeedStatus(status)+",0,1),"
                        # output progress occasionally to make sure code is not
                        # stalling
                        num_processed += 1
                        if num_processed % 100 == 0:
                            print "%s Statuses Processed: %s" %  (num_processed, datetime.datetime.now())
                    # if there is no next page, we're done.
                    if 'paging' in statuses.keys():
                        statuses = json.loads(request_until_succeed(statuses['paging']['next']))
                    else:
                        has_next_page = False

            print "\nDone!\n%s Statuses Processed in %s" % (num_processed, datetime.datetime.now() - scrape_starttime)
    sql = sql[:-1]
    try:
        # Execute the SQL command
        cursor.execute(sql)
        # Commit your changes in the database
        db.commit()
        print"Success"
    except:
        # Rollback in case there is any error
        db.rollback()
        print"Fail"
except:
    print "Error: unable to query data"

# disconnect from server
db.close()