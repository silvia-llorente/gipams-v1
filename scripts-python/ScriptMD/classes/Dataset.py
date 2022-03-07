import xml.etree.ElementTree as ET

class Dataset:
    def __init__(self,Title,sample,schemaPath):
        self.Title = Title
        self.sample = sample
        self.schemaPath = schemaPath

    def write(self,outPath):
        dgmtRoot = ET.Element('DatasetMetadata')
        dgmtRoot.set('xmlns','urn:mpeg:mpeg-g:metadata_encrypted:dataset:2019')
        dgmtRoot.set('xmlns:xsi','http://www.w3.org/2001/XMLSchema-instance')
        dgmtRoot.set('xsi:schemaLocation','urn:mpeg:mpeg-g:metadata_encrypted:dataset:2019 {}'.format(self.schemaPath))
        elem = ET.SubElement(dgmtRoot,'Title')
        elem.text = self.Title
        samples = ET.SubElement(dgmtRoot,'Samples')
        sampleE = ET.SubElement(samples,'Sample')
        for item in self.sample.__dict__.items():
            if not item[0][0].islower() and item[1] != None:
                elem = ET.SubElement(sampleE,item[0])
                elem.text = item[1]
        if self.sample.extensions != None:
            extensionsE = ET.SubElement(sampleE,'Extensions')
            for extension in self.sample.extensions:
                extensionE = ET.SubElement(extensionsE,'Extension')
                extension.write(extensionE)

        ET.ElementTree(dgmtRoot).write(outPath,encoding='utf-8', xml_declaration=True)