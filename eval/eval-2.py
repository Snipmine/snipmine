#!/usr/bin/env python3

from pymysql import connect
from pymysql.cursors import DictCursor
from re import sub
from statistics import median, mean
from json import dumps
import random

con = connect(host='localhost', user='snipcloud', password='', db='snipcloud_c3', cursorclass=DictCursor)

minuse = 5


class Snippet:
    def __init__(self, sid, code, uc):
        self.id = sid
        self.code = code
        self.usecount = uc
        self.tags = set()

    def addUse(self, use):
        self.usecount += use

    def addTag(self, tag):
        self.tags.add(tag)

    def setTags(self, tags):
        self.tags = set(tags)

    def json(self):
        return dumps((self.id, self.code, self.usecount, list(self.tags)), ensure_ascii=True, indent=2)


def query(q, params, con):
    with con.cursor() as cursor:
        cursor.execute(q, params)
        result = cursor.fetchall()
    con.commit()
    return result


def getSnippets(c, idrange):
    snippets = dict()
    tmp = '(' + ', '.join([str(x) for x in idrange]) + ')'
    with c.cursor() as cursor:
        cursor.execute("SELECT s.id, s.code, s.usecount, t.tag FROM ((snippets s JOIN hastag h on s.id = h.snippet) JOIN tags t ON h.tag = t.id)")
        for s in cursor:
            if not s['id'] in snippets:
                snippets[s['id']] = Snippet(s['id'], s['code'], s['usecount'])
            snippets[s['id']].addTag(s['tag'])
    c.commit()
    return [x for x in snippets.values()]

try:
    rnd = [random.randrange(699329)+1 for i in range(4000)]
    snippets = getSnippets(con, rnd)
    random.shuffle(snippets)
    srcfiles = list()
    result = list()
    while len(result) < 100:
        s = snippets.pop()
        for t in s.tags:
            if t.startswith('sm:src:'):
                t = t[len('sm:src:/home/alex/bachelor/eval2/repos/'):]
                if not t in srcfiles:
                    srcfiles.append(t)
                    result.append(s)
                    continue
    print('[\n' + ','.join([x.json() for x in result]) + '\n]')


finally:
    con.close()
