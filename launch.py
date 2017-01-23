import subprocess
import sys

start = int(sys.argv[1])
input_file_name = sys.argv[2]

for idx in range(start, start + 40, 4):
    process = subprocess.Popen([
        'qsub',
        '-l', 'nodes=1:ppn=12',
        '-A', 'intob2016',
        '-l', 'walltime=8:00:00',
        '-F', '"{0} {1} {2}"'.format(str(idx), str(idx+4), input_file_name),
        '/people/plgpgoralczyk/IntObl2016/main.sh'])
    process.communicate()

