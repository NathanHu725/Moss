# Moss
    Summer work on Moss implementation and trying to find ways to improve it.

## Moss steps:
1. Strip comments and "tokenize" variable/method names.
    - This work is done in the `SimpleLexer` class
2. Fingerprint the document with the winnowing algorithm according the Aiken doc
    - http://theory.stanford.edu/~aiken/publications/papers/sigmod03.pdf
3. Compare pairs of fingerprints with some predertimined mechanism
    - Jaccard coefficient
    - Sørensen–Dice coefficient
    - Tversky index

## To Do:
 - [x] Interface for using MOSS
 - [ ] Choose standard t and k values for fingerprinting
 - [ ] Update SimpleLexer to more smartly parse Java files
 - [ ] Approach dealing with attacks
 - [ ] Scrape publicly available code and store intermediate representations
 - [ ] Use TF-IDF to give less weight to common k-gram / hash values
 - [ ] Come up with other todos
 
## Common Attacks (listed in Ohmann, 2013)
    https://digitalcommons.csbsju.edu/cgi/viewcontent.cgi?article=1015&context=honors_theses
 - Changing comments or formatting
 - Changing identifiers
 - Changing the order of operands in expressions
 - Changing data types
 - Replacing expressions by equivalents
 - Adding redundant statements or variables
 - Changing the order of independent statements
 - Changing the structure of iteration statements
 - Changing the structure of selection statements
 - Replacing procedure calls by the procedure body
 - Introducing non-structured statements
 - Combining original and copied program fragments
