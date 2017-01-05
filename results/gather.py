import csv

for idx in range(0, 400, 4):
	try:
		fd = open('result_{0}_{1}.txt'.format(str(idx), str(idx+4)))
		reader = csv.reader(fd, delimiter=';')
		for row in reader:
			print ','.join(map(lambda x: x.strip(), row[:-1]))
	except:
		pass

