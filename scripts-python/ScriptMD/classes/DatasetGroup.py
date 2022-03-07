import xml.etree.ElementTree as ET
from classes.Dataset import Dataset
import os

class DatasetGroup:
    def __init__(self,schemaPath,datasetSchemaPath):
        self.Title = None
        self.Type = None
        self.Abstract = None
        self.ProjectCentreName = None
        self.Description = None
        self.samples = []
        self.extensions = None
        self.schemaPath = schemaPath
        self.datasetSchemaPath = datasetSchemaPath

    def addExtension(self,extension):
        if self.extensions == None:
            self.extensions = [extension]
        else:
            self.extensions.append(extension)

    def write(self,outPath):
        dgmtRoot = ET.Element('DatasetGroupMetadata')
        dgmtRoot.set('xmlns','urn:mpeg:mpeg-g:metadata_encrypted:dataset_group:2019')
        dgmtRoot.set('xmlns:xsi','http://www.w3.org/2001/XMLSchema-instance')
        dgmtRoot.set('xsi:schemaLocation','urn:mpeg:mpeg-g:metadata_encrypted:dataset_group:2019 {}'.format(self.schemaPath))
        for item in self.__dict__.items():
            #Noms en minuscula no van directament al XML
            if not item[0][0].islower() and item[1] != None:
                if item[0] == 'ProjectCentreName':
                    elem = ET.SubElement(dgmtRoot,'ProjectCentre')    
                    elem = ET.SubElement(elem,'ProjectCentreName')
                    elem.text = item[1]
                elif item[0] == 'Type':
                    elem = ET.SubElement(dgmtRoot,'Type')
                    elem.set('existing_study_type',item[1])
                else:
                    elem = ET.SubElement(dgmtRoot,item[0])
                    elem.text = item[1]
        
        samples = ET.SubElement(dgmtRoot,'Samples')
        it = 0
        for sample in self.samples:
            dt = Dataset(self.Title,sample,self.datasetSchemaPath)
            dt.write(os.path.join(outPath,'dtmt_{}.xml'.format(it)))
            sampleE = ET.SubElement(samples,'Sample')
            for item in sample.__dict__.items():
               if not item[0][0].islower() and item[1] != None:
                    elem = ET.SubElement(sampleE,item[0])
                    elem.text = item[1]
            if sample.extensions != None:
                extensionsE = ET.SubElement(sampleE,'Extensions')
                for extension in sample.extensions:
                    extensionE = ET.SubElement(extensionsE,'Extension')
                    extension.write(extensionE)
            it += 1

        ET.ElementTree(dgmtRoot).write(os.path.join(outPath,'dgmt.xml'),encoding='utf-8', xml_declaration=True)