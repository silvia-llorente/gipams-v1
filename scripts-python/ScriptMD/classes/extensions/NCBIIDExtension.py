from classes.BaseExtension import BaseExtension
import xml.etree.ElementTree as ET

class NCBIIDExtension(BaseExtension):
    def __init__(self,URN,filename,name):
        BaseExtension.__init__(self,URN,filename,name)
        self.SampleName = None

    def write(self,xmlElem):
        if xmlElem.tag != 'Extension':
            raise ValueError('Element a escriure no és una Extension')
        typeE = ET.SubElement(xmlElem,'Type')
        typeE.text = self.urn
        extE = ET.SubElement(xmlElem,self.name)
        extE.set('xmlns',self.urn)
        extE.set('xmlns:xsi','http://www.w3.org/2001/XMLSchema-instance')
        extE.set('xsi:schemaLocation','{} {}'.format(self.urn,self.filename))
        elem = ET.SubElement(extE,'SampleName')
        elem.text = self.SampleName

    def parse(self,xmlElem):
        if xmlElem.tag != 'Ids':
            raise ValueError('Element a llegir no és Ids')

        for idE in xmlElem.findall('Id'):
            if 'db_label' in idE.attrib and idE.attrib['db_label'] == 'Sample name':
                self.SampleName = idE.text
                return True
        return False
            