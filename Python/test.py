import random
def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False
def bin_to_int(binary):
            return int(binary, 2)
number=6
res=""
    # if it is not a digit it is a sign
if is_number(number):
    new_nb = ""
    # convert the integer number in a bit string
    l='{0:b}'.format(int(number))
    for bin in '{0:b}'.format(int(number)):
        value = int(bin)
        if random.random() < 0.1:
            value = 1-value
        new_nb += str(value)
    res += str(bin_to_int(new_nb))
else:
    # randomize the new sign
    if random.random() > 0.1 or number == "-":
        res += number
print(res)
