#!/usr/bin/env python3

from pymysql import connect
from pymysql.cursors import SSDictCursor
from re import sub

cona = connect(host='localhost', user='snipcloud', password='', db='snipcloud_a2', cursorclass=SSDictCursor)
conb = connect(host='localhost', user='snipcloud', password='', db='snipcloud_c2', cursorclass=SSDictCursor)


class Snippet:
    def __init__(self, code, uc):
        self.code = code
        self.usecount = uc
        self.tags = set()

    def addUse(self, use):
        self.usecount += use

    def addTag(self, tag):
        self.tags.add(tag)

    def setTags(self, tags):
        self.tags = set(tags)


def query(q, params, con):
    with con.cursor() as cursor:
        cursor.execute(q, params)
        result = cursor.fetchall()
    con.commit()
    return result


def matchCode(snip_a, snip_b):
    blur = lambda x: sub('`.*?`', '', sub('\s', '', x))
    return blur(snip_a.code) == blur(snip_b.code)


def mergeTags(snip_a, snip_b):
    tagsa = snip_a.tags
    tagsb = snip_b.tags
    result = set([x for x in tagsb if not x.startswith('sm:import:')])
    imps = [x.split(',') for x in tagsa if x.startswith('sm:import:')] + [x.split(',') for x in tagsb if x.startswith('sm:import:')]
    tmp = dict()
    for imp in imps:
        if imp[0] in tmp:
            tmp[imp[0]] += int(imp[1])
        else:
            tmp[imp[0]] = int(imp[1])
    for imp, wgt in tmp.items():
        result.add(imp + ',' + str(wgt))
    return result


def getSnippets(con):
    snippets = dict()
    byHash = dict()
    with con.cursor() as cursor:
        cursor.execute("SELECT s.id, s.code, s.usecount, t.tag FROM ((snippets s JOIN hastag h on s.id = h.snippet) JOIN tags t ON h.tag = t.id)")
        for s in cursor:
            if not s['id'] in snippets:
                snippets[s['id']] = Snippet(s['code'], s['usecount'])
            snippets[s['id']].addTag(s['tag'])
            if s['tag'].startswith('sm:hc:'):
                if not s['tag'] in byHash:
                    byHash[s['tag']] = []
                byHash[s['tag']].append(snippets[s['id']])
    con.commit()
    del snippets
    return byHash


try:
    print('Loading all snippets in A')
    snippetsa = getSnippets(cona)
    print('Loading all snippets in B')
    snippetsb = getSnippets(conb)
    print('Loaded all snippets...')

    for hc in snippetsa:
        for s in snippetsa[hc]:
            if not hc in snippetsb:
                snippetsb[hc] = []
            match = next([x for x in snippetsb[hc] if matchCode(s, x)].__iter__(), None)
            if match:
                #update tags and usecount of matching snippet in b
                match.addUse(s.usecount + 1)
                match.setTags(mergeTags(s, match))
            else:
                #Add this snippet
                snippetsb[hc].append(s)
    del snippetsa
    print('Computed new snippets...')

    query('TRUNCATE TABLE snippets', None, conb)
    query('TRUNCATE TABLE hastag', None, conb)
    query('TRUNCATE TABLE tags', None, conb)
    print('Cleared target db...')

    tagmap = set([z for x in snippetsb.values() for y in x for z in y.tags])
    with conb.cursor() as cursor:
        cursor.executemany("INSERT INTO tags (tag) VALUES (%s)", tagmap)
    conb.commit()
    tagmap = dict()
    for t in query("SELECT id, tag FROM tags", None, conb):
        tagmap[t['tag']] = t['id']
    print('Added new tags...')

    nextid = 1
    hastags = []
    ss = []
    for s in [y for x in snippetsb.values() for y in x]:
        ss.append((nextid, s.code, s.usecount))
        for ht in [(nextid, tagmap[x]) for x in s.tags]:
            hastags.append(ht)
        nextid += 1
    del snippetsb
    with conb.cursor() as cursor:
        cursor.executemany("INSERT INTO snippets (id, code, usecount, author) VALUES (%s, %s, %s, 1)", ss)
    conb.commit()
    del ss
    print('Added snippets...')

    with conb.cursor() as cursor:
        cursor.executemany("INSERT INTO hastag (snippet, tag) VALUES (%s, %s)", hastags)
    conb.commit()
    print('added hastag entries...')

finally:
    cona.close()
    conb.close()
