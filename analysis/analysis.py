import json
import matplotlib.pyplot as plt
import numpy as np

# Traitement des données

with open('nb-answers-bin-1M-queries-500k-triplets.json', 'r') as f:
    nombreReponses1Mqueries500ktriplets = json.load(f)

names = ['0-1', '1-20', '20-100', '100+']
values = [nombreReponses1Mqueries500ktriplets[n] for n in names]

# Horizontal legend (x-axis label)
plt.xlabel("Nombre de réponses par requête (500k de triplets / 1 million de requêtes)")

# Vertical legend (y-axis label)
plt.ylabel("Nombre de requêtes")
plt.bar(names, values, color='green')
plt.tight_layout()

plt.savefig('figures/nb-answers-bin-1M-queries-500k-triplets.png')
plt.close()

with open('nb-sample-answers-bin-1M-queries-500k-triplets.json', 'r') as f:
    nombreReponsesSample1Mqueries500ktriplets = json.load(f)

names = ['0-1', '1-20', '20-100', '100+']
values = [nombreReponsesSample1Mqueries500ktriplets[n] for n in names]

# Horizontal legend (x-axis label)
plt.xlabel("Nombre de réponses par requête (500k de triplets / 1 million de requêtes) après échantillonnage")

# Vertical legend (y-axis label)
plt.ylabel("Nombre de requêtes")
plt.bar(names, values, color='orange')
plt.tight_layout()

plt.savefig('figures/nb-sample-answers-bin-1M-queries-500k-triplets.png')
plt.close()

## Analyse des performances

with open('loading-time-statistics.json', 'r') as f:
    loadingTimeStats = json.load(f)

names = ['HEXASTORE', 'GIANT-TABLE']
namesTranslated = ['HEXASTORE', 'GIANT-TABLE']

values = [loadingTimeStats[n]*(1e-9) for n in names]

# Horizontal legend (x-axis label)
plt.xlabel("Type de moteur de requêtes")

# Vertical legend (y-axis label)
plt.ylabel("Temps de chargement (s)")
plt.bar(namesTranslated, values, color='orange')

plt.title('Temps de chargement par moteur de requêtes')

plt.savefig('figures/loading-time-statistics.png')
plt.close()

with open('performance-statistics-CONCUR-HEXASTORE-500k-1M-queries.json', 'r') as f:
    performanceHexastoreConcurrent = json.load(f)

names = ['0-1', '1-20', '20-100', '100+']

means = [performanceHexastoreConcurrent[name]['mean']*(1e-6)  for name in names]
stds = [performanceHexastoreConcurrent[name]['std']*(1e-6)  for name in names]

x = np.arange(len(names))
plt.figure(figsize=(8, 5))
plt.bar(x, means, yerr=stds, capsize=5, color='skyblue', edgecolor='black')
plt.xlabel("Nombre de réponses par requête")
plt.xticks(x, names)
plt.ylabel('Temps de réponse (ms)')
plt.title('Temps de réponse par nombre de réponses par requête (Hexastore concurrent / 500k triplets)')

plt.savefig('figures/performance-statistics-CONCUR-HEXASTORE-500k-1M-queries.png')
plt.close()

with open('performance-statistics-HEXASTORE-500k-1M-queries.json', 'r') as f:
    performanceHexastore = json.load(f)
with open('performance-statistics-CONCUR-HEXASTORE-500k-1M-queries.json', 'r') as f:
    performanceConcurrentHexastore = json.load(f)

names = ['0-1', '1-20', '20-100', '100+']

meansHexa = [performanceHexastore[name]['mean']*(1e-6) for name in names]
stdsHexa = [performanceHexastore[name]['std']*(1e-6) for name in names]

meansConcurHexa = [performanceConcurrentHexastore[name]['mean']*(1e-6) for name in names]
stdsConcurHexa = [performanceConcurrentHexastore[name]['std']*(1e-6) for name in names]

width = 0.35

x = np.arange(len(names))

plt.bar(x - width / 2 - 0.025, meansHexa, width, yerr=stdsHexa, capsize=5, color='skyblue', label='Hexastore', edgecolor='black')
plt.bar(x + width / 2 + 0.025, meansConcurHexa, width, yerr=stdsConcurHexa, capsize=5, color='orange', label='Hexastore concurrent', edgecolor='black')

plt.xlabel("Nombre de réponses par requête")
plt.xticks(x, names)
plt.ylabel('Temps de réponse (ms)')
plt.title('Hexastore concurrent VS Hexastore')

plt.legend(loc="upper left")

plt.savefig('figures/performance-statistics-HEXASTORE-500k-1M-queries-comparaison.png')
plt.close()


with open('performance-statistics-CONCUR-GIANT-TABLE-500k-1M-queries.json', 'r') as f:
    performanceGiantTableConcurrent = json.load(f)

names = ['0-1', '1-20', '20-100', '100+']

means = [performanceGiantTableConcurrent[name]['mean'] for name in names]
stds = [performanceGiantTableConcurrent[name]['std'] for name in names]

x = np.arange(len(names))
plt.figure(figsize=(8, 5))
plt.bar(x, means, yerr=stds, capsize=5, color='red', edgecolor='black')
plt.xlabel("Nombre de réponses par requête")
plt.xticks(x, names)
plt.ylabel('Temps de réponse')
plt.title('Temps de réponse par nombre de réponses par requête (GiantTable concurrent / 500k triplets)')

plt.savefig('figures/performance-statistics-CONCUR-GIANT-TABLE-500k-1M-queries.png')
plt.close()

with open('performance-statistics-HEXASTORE-2M-triplets-1M-queries.json', 'r') as f:
    performanceHexastore2m = json.load(f)
with open('performance-statistics-CONCUR-HEXASTORE-2M-triplets-1M-queries.json', 'r') as f:
    performanceConcurrentHexastore2m = json.load(f)

names = ['0-1', '1-20', '20-100', '100+']

meansHexa2m = [performanceHexastore2m[name]['mean']*(1e-6) for name in names]
stdsHexa2m = [performanceHexastore2m[name]['std']*(1e-6) for name in names]

meansConcurHexa2m = [performanceConcurrentHexastore2m[name]['mean']*(1e-6) for name in names]
stdsConcurHexa2m = [performanceConcurrentHexastore2m[name]['std']*(1e-6) for name in names]

width = 0.35

x = np.arange(len(names))

plt.bar(x - width / 2 - 0.025, meansHexa2m, width, yerr=stdsHexa2m, capsize=5, color='skyblue', label='Hexastore', edgecolor='black')
plt.bar(x + width / 2 + 0.025, meansConcurHexa2m, width, yerr=stdsConcurHexa2m, capsize=5, color='orange', label='Hexastore concurrent', edgecolor='black')

plt.xlabel("Nombre de réponses par requête")
plt.xticks(x, names)
plt.ylabel('Temps de réponse (ms)')
plt.title('Hexastore concurrent VS Hexastore (2 millions de triplets)')

plt.legend(loc="upper left")

plt.savefig('figures/performance-statistics-HEXASTORE-2M-1M-queries-comparaison.png')
plt.close()


