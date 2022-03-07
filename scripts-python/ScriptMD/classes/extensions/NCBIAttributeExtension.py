from classes.BaseExtension import BaseExtension
import xml.etree.ElementTree as ET

class NCBIAttributeExtension(BaseExtension):
    def __init__(self,URN,filename,name):
        BaseExtension.__init__(self,URN,filename,name)
        self.StudyDesign = None
        self.BodySite = None
        self.AnalyteType = None
        self.IsTumor = None
        self.itemList = {'study design':'StudyDesign','body site':'BodySite','analyte type':'AnalyteType','is tumor':'IsTumor'}

    def write(self,xmlElem):
        if xmlElem.tag != 'Extension':
            raise ValueError('Element a escriure no és una Extension')
        typeE = ET.SubElement(xmlElem,'Type')
        typeE.text = self.urn
        extE = ET.SubElement(xmlElem,self.name)
        extE.set('xmlns',self.urn)
        extE.set('xmlns:xsi','http://www.w3.org/2001/XMLSchema-instance')
        extE.set('xsi:schemaLocation','{} {}'.format(self.urn,self.filename))
        for item in self.__dict__.items():
            if not item[0][0].islower():
                elem = ET.SubElement(extE,item[0])
                elem.text = item[1]

    def parse(self,xmlElem):
        found = False
        if xmlElem.tag != 'Attributes':
            raise ValueError('Element a llegir no és Attributes')

        for attribute in xmlElem.findall('Attribute'):
            if attribute.attrib['attribute_name'] in self.itemList:
                self.__dict__.update({self.itemList[attribute.attrib['attribute_name']]:attribute.text})
                found = True
                
        return found