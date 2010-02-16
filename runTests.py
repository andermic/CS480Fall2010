import os
import sys

def main():
	if len(sys.argv) != 2:
		print "Usage: runTests.py [ASSIGNMENT NAME]"
	else:
		contents = os.listdir(os.getcwd())
		aName = sys.argv[1]
		os.system('clear')
		for file in contents:
			if (file[:4] == 'test') and ((file + '.s') in contents):
				os.system('echo Running ' + file)
				os.system('java %s %s > %s' % (aName, file, file + '.our'))
				os.system('echo >> testResults.tmp')
				os.system('echo >> testResults.tmp')
				os.system('echo "********************************************************************************" >> testResults.tmp')
				os.system('echo "diff %s %s:" >> testResults.tmp' % (file + '.s', file + '.our'))
				os.system('diff %s %s >> testResults.tmp' % (file + '.s', file + '.our'))
				os.system('echo "********************************************************************************" >> testResults.tmp')

		stream = open('testResults.tmp', 'r')
		for line in stream.readlines():
			print line,
		os.system('rm -rf testResults.tmp')

main()
