# ResAulaSenato2ParlaMintTEI
Encoding of parliamentary transcript of the Italian Senate to ParlaMint TEI


# phase 1 - source preprocessing and cleaning

1. table **unique** files from SOURCE (corpus HTML Senato)
	- *input:* data/Resaula-ParlaMINT/SOURCE
	- *output:* data/unique_grid.csv

2. extract *embedded XML* annotation from HTML source and save *embedded XML*  
	- remove html annotations, clean and unescape text
	- check well-formedness of *embedded XML*
	- *output:* data/Resaula-ParlaMINT/Resaula-embedded-XML

3. manually enforce well-formedness of not well-formed files (62/1200)

- launch RunHTMLSenato2XML.java


# phase 2 - XML 2 TEI conversion

1. data on Senators, Groups, affiliations: 
	- from dati.senato.it and from open sources
	- TSV in /data/Resaula-ParlaMINT/dati_senato
	

2. create ParlaMint_IT corpus Root from data 

3. convert *embedded XML* to ParlaMint TEI 
 	- parse and analyze pre-existing annotations
 	- convert to ParlaMint TEI annotations
	- *output:* data/Resaula-ParlaMINT/Resaula-TEI-output

- launch RunXMLSenato2TEI.java



## changelog - post export (05/2021)


## manual fix
- filePath in xInclude for ANA corpus
- fix covid/reference ottobre/novembre 2019
- coalition/opposition relations
- HOUSES (parla.bi, parla.upper) (textClass/catRef in ROOTS files)
- legislature annotation in meetings (LEG.17 / LEG.18)
- data fine del Governo Conte 


FIX PARLAMINT HOUSES

```
Replace ALL:

<meeting ana="#parla.upper #parla.term" n="18-upper">

<meeting ana="#parla.upper #parla.term #LEG.18" n="18-upper">

and

<meeting ana="#parla.upper #parla.term" n="17-upper">

<meeting ana="#parla.upper #parla.term #LEG.17" n="17-upper">
```



```
- parla.term in component files (LEG.17 / LEG.18)
- <meeting ana="#parla.upper #parla.term #LEG.17" n="17-upper">
- <meeting ana="#parla.upper #parla.term #LEG.18" n="18-upper">
```

```
<textClass>
  <catRef scheme="#parla.legislature" target="#parla.bi #parla.upper"/>
</textClass>
```

==========================

Essentially, all the meeting elements of the corpus root and of each component should have in their @ana attribute added the pointer to the appropriate house, so (assuming you did not 
change the taxonomy IDs) either #parla.upper or #parla.lower, e.g. for 

```
SI root it would be:
<meeting n="7" ana="#parla.lower #parla.term #DZ.7">7. mandat</meeting>

<meeting n="8" ana="#parla.lower #parla.term #DZ.8">8. mandat</meeting>

and for a SI component:
<meeting n="59" ana="#parla.lower #parla.meeting.extraordinary">Izredna</meeting>

<meeting n="7" ana="#parla.lower #parla.term #DZ.7">7. mandat</meeting>
```