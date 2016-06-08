#!/usr/bin/env python3
import random, json

with open('dataset.json', 'r') as f:
    data = json.load(f)
pop_size = 10
pop = []
cut = 50

def switch(split):
    copy = (list(split[0]), list(split[1]))
    l = random.choice(copy[0])
    r = random.choice(copy[1])
    copy[0].remove(l)
    copy[0].append(r)
    copy[1].remove(r)
    copy[1].append(l)
    return copy

def gen_split(data):
    copy = list(data)
    random.shuffle(copy)
    return (copy[:int(len(copy)/2)], copy[int(len(copy)/2):])

def rate(split):
    max_sloc = 1271273
    slocl = [x['sloc'] for x in split[0]]
    slocr = [x['sloc'] for x in split[1]]
    sloc_diff = abs(sum(slocl)-sum(slocr))
    return sloc_diff

best = 999999999
runs = 0

while True:
    for i in range(pop_size):
        pop.append(gen_split(data))
    for i in range(5):
        pop = pop + [switch(x) for x in pop]
    pop.sort(key=rate)
    pop = pop[:pop_size]
    r = rate(pop[0])
    if r >= best:
        runs += 1
    else:
        best = r
        runs = 0
        print()
        print(pop[0], r)
    if runs >= cut:
        print("No improvement for",cut,"iterations")
        break
with open('datasplit_a.json', 'w') as f:
    json.dump(pop[0][0], f, indent=2, ensure_ascii=True)
with open('datasplit_b.json', 'w') as f:
    json.dump(pop[0][1], f, indent=2, ensure_ascii=True)
with open('datasplit_a.txt', 'w') as f:
    for x in pop[0][0]:
        f.write(x['name'] + '\n')
with open('datasplit_b.txt', 'w') as f:
    for x in pop[0][1]:
        f.write(x['name'] + '\n')
