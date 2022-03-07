from classes.Organization import Organization
from classes.DatasetGroup import DatasetGroup
from classes.Sample import Sample
from classes.extensions import NCBIOrganizationExtension
import xml.etree.ElementTree as ET

class ENA(Organization):
    def __init__(self,dgmtPath,dtmtPath,extensionPath):
        Organization.__init__(self,dgmtPath,dtmtPath,extensionPath)

    def parse(self,Study,Assembly):
        dg = self.parseStudy(ET.parse(Study))
        dg.samples = self.parseAssembly(ET.parse(Assembly))
        return dg

    def parseAssembly(self,assembly):
        root = assembly.getroot()
        if root.tag != 'ASSEMBLY_SET':
            raise ValueError('Assembly invàlid')
        #A partir d'aqui assumeixo que és un Assembly vàlid
        samples = []
        for assembly in root.findall('ASSEMBLY'):
            sample = Sample()
            taxon = assembly.find('TAXON')
            taxonId = taxon.find('TAXON_ID')
            sample.TaxonId = taxonId.text
            title = assembly.find('TITLE')
            sample.Title = title.text
            samples.append(sample)
        return samples

    def parseStudy(self,study):
        root = study.getroot()
        dg = DatasetGroup(self.dgmtPath,self.dtmtPath)
        if root.tag != 'STUDY_SET':
            raise ValueError('Study invàlid')
        #A partir d'aqui assumeixo que és un Study vàlid
        #STUDY Element parsing
        study = root.find('STUDY')
        if 'center_name' in study.attrib:
            dg.ProjectCentreName = study.attrib['center_name']
        descriptor = study.find('DESCRIPTOR')
        title = descriptor.find('STUDY_TITLE')
        dg.Title = title.text
        typeE = descriptor.find('STUDY_TYPE')
        dg.Type = typeE.attrib['existing_study_type']
        abstract = descriptor.find('STUDY_ABSTRACT')
        if abstract != None:
            dg.Abstract = abstract.text
        description = descriptor.find('STUDY_DESCRIPTION')
        if description != None:
            dg.Description = description.text
        return dg