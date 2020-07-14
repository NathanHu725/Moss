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
 - [ ] Come up with other todos