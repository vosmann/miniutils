import sys

histogram = dict()

bin_width = 5
max_index = 0

for line in sys.stdin:
    if not line:
        continue

    number = int(line)
    bin_index = number / bin_width
    if bin_index not in histogram:
        histogram[bin_index] = 0
    histogram[bin_index] = histogram[bin_index] + 1
    if bin_index > max_index:
        max_index = bin_index

for index in range(max_index) + [max_index + 1]:
    if index not in histogram:
        histogram[index] = 0
    count = histogram[index]
    if count == None:
        count = 0
    print "[{0}, {1}> : {2}".format(index * bin_width, (index + 1) * bin_width, count)
