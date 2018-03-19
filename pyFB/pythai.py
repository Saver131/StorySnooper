import json
import csv
import re
import pandas as pd
from sklearn.naive_bayes import MultinomialNB
from sklearn.feature_extraction.text import CountVectorizer
from sklearn import metrics
from sklearn.model_selection import train_test_split

trains = pd.read_csv("commentRes.csv",sep=',',header=None,error_bad_lines=False)

with open("commentRes.csv",'r') as file:
	trains = list(csv.reader(file))

vectorizer = CountVectorizer(max_features = 10000)

train,test= train_test_split(trains,test_size=0.3)

train_features = vectorizer.fit_transform([r[0] for r in train])
test_features = vectorizer.transform([r[0] for r in test])

nb = MultinomialNB()
nb.fit(train_features, [int(r[1]) for r in train])

predictions = nb.predict(test_features)

actual =[int(r[1]) for r in test]

fpr,tpr,thresholds = metrics.roc_curve(actual,predictions,pos_label=1)
print ("Multinomial naive bayes AUC: {0}".format(metrics.auc(fpr,tpr)))

		