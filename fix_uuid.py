#!/usr/bin/env python
import re

filepath = r'D:\WS\java\DTInvoice\src\resources\reports\new_dt_invoice_final.jrxml'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Remove all uuid attributes
content = re.sub(r' uuid="[^"]*"', '', content)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print('Successfully removed all UUID attributes')

