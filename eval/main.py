#!/usr/bin/env python3
from requests import get
from time import sleep
from json import loads as fromjson, dump as tojsonf
from sh import git, cd, pwd

def load(url, retries=8):
    r = get(url + '?access_token=')
    if r.status_code != 200:
        if retries < 0:
            raise Exception('loading failed: ' + str(r.status_code))
        else:
            sleep(1)
            return load(url, retries-1)
    else:
        return r.text
data = list()
with open('dataset.txt', 'r') as f:
    for repo in [x.replace('\n', '') for x in f.readlines()]:
        print('Loading', repo)
        r = fromjson(load('https://api.github.com/repos/' + repo))
        data.append({'name': repo, 'forks': r['forks'], 'watchers': r['watchers'], 'size': r['size'], 'ssh_url': r['ssh_url'], 'before': 'May 16 2016'})

dir = str(pwd())[:-1] + '/repos'
for r in data:
    cd(dir)
    try:
        print('Cloning', r['ssh_url'])
        git.clone(r['ssh_url'], r['name'])
        cd(r['name'])
        sha = str(git('rev-list', '-1', '--before="' + r['before'] + '"', 'HEAD'))[:-1]
        print('Checking out', sha)
        git.checkout(sha)
        r['commit'] = sha
    except:
        r['status'] = 'FAILED'
cd(dir)
cd('..')

with open('dataset.json', 'w') as f:
    tojsonf(data, f, ensure_ascii=True, indent=2)

