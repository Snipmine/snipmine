#!/usr/bin/env python3

from pymysql import connect
from pymysql.cursors import DictCursor
from re import sub
from statistics import median, mean
from json import dumps

cona = connect(host='localhost', user='snipcloud', password='', db='snipcloud_a3', cursorclass=DictCursor)
conb = connect(host='localhost', user='snipcloud', password='', db='snipcloud_b3', cursorclass=DictCursor)

minuse = 2

def query(q, params, con):
    with con.cursor() as cursor:
        cursor.execute(q, params)
        result = cursor.fetchall()
    con.commit()
    return result

def matchCode(snip_a, snip_b):
    blur = lambda x: sub('`.*?`', '', sub('\s', '', x))
    return blur(snip_a['code']) == blur(snip_b[0])

try:
    frequencies = [x['usecount']+1 for x in query("SELECT usecount FROM snippets WHERE usecount >= " + str(minuse), None, cona)]
    print('Mean frequency:', mean(frequencies))
    print('Median frequency:', median(frequencies))
    print('Max frequency:', max(frequencies))
    print('Min frequency:', min(frequencies))
    print('#Snippets:', len(frequencies))
    del frequencies

    print('Computing hashsets...')
    snippetsa = query("SELECT s.code, s.usecount, t.tag FROM ((snippets s JOIN hastag h on s.id = h.snippet) JOIN tags t ON h.tag = t.id) WHERE t.tag LIKE 'sm:hc:%%' AND usecount >= %s", (minuse,), cona)
    snippetsb = dict()
    for s in query("SELECT s.code, s.usecount, t.tag FROM ((snippets s JOIN hastag h on s.id = h.snippet) JOIN tags t ON h.tag = t.id) WHERE t.tag LIKE 'sm:hc:%%'", None, conb):
        if not s['tag'] in snippetsb:
            snippetsb[s['tag']] = list()
        snippetsb[s['tag']].append((s['code'], s['usecount']))
    print("Done")

    result = dict()
    for s in snippetsa:
        try:
            match = next([x for x in snippetsb[s['tag']] if matchCode(s, x)].__iter__(), None)
        except KeyError:
            match = None
        if not s['usecount']+1 in result:
            result[s['usecount']+1] = {'found':0,'not found':0, 'other freq sum':0}
        if match:
            r = result[s['usecount']+1]
            r['found'] += 1
            r['other freq sum'] += (match[1]+1)
        else:
            result[s['usecount'] + 1]['not found'] += 1
    print('Frequency, Found, Not found, Freqsum')
    for f in result:
        print(', '.join((str(f), str(result[f]['found']), str(result[f]['not found']), str(result[f]['other freq sum']))))
finally:
    cona.close()
    conb.close()
