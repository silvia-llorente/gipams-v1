from classes.Organization import Organization
from classes.DatasetGroup import DatasetGroup
from classes.Sample import Sample
from classes.extensions.NCBIAttributeExtension import NCBIAttributeExtension
from classes.extensions.NCBIIDExtension import NCBIIDExtension
import xml.etree.ElementTree as ET

class NCBI(Organization):
    def __init__(self,dgmtPath,dtmtPath,extensionPath):
        Organization.__init__(self,dgmtPath,dtmtPath,extensionPath)

    def parse(self,BioProject,BioSample):
        dg = self.parseBioProject(ET.parse(BioProject))
        dg.samples = self.parseBioSample(ET.parse(BioSample))
        return dg

    def parseBioProject(self,bp):
        root = bp.getroot()
        dg = DatasetGroup(self.dgmtPath,self.dtmtPath)
        if root.tag != 'DocumentSummary':
            raise ValueError('BioProject invàlid')
        #A partir d'aqui assumeixo que és un BioProject vàlid
        #Project parsing
        project = root.find('Project')
        projectDescr = project.find('ProjectDescr')
        title = projectDescr.find('Title')
        dg.Title = title.text
        description = projectDescr.find('Description')
        if description != None:
            dg.Description = description.text
        #Submission parsing
        submission = root.find('Submission')
        description = submission.find('Description')
        #En el DatasetGroup base només es pot tenir una organització, així que agafo la primera
        organizationList = description.findall('Organization')
        dg.ProjectCentreName = organizationList[0].find('Name').text
        return dg
    
    def parseBioSample(self,bs):
        samples = []
        root = bs.getroot()
        samples = []
        if root.tag != 'BioSampleSet':
            raise ValueError('BioSample invàlid')
        #A partir d'aqui assumeixo que és un BioProject vàlid
        #He vist description al exemple però descriptor al xsd
        for biosample in root.findall('BioSample'):
            sample = Sample()
            description = biosample.find('Description')
            title = description.find('Title')
            if title != None:
                sample.Title = title.text
            organism = description.find('Organism')
            if 'taxonomy_id' in organism.attrib:
                sample.TaxonId = organism.attrib['taxonomy_id']
            attributes = biosample.find('Attributes')
            if attributes != None:
                extension = NCBIAttributeExtension('urn:mpeg:mpeg-g:metadata:extension:ncbi',self.extensionPath,'AttributeExtension')
                if extension.parse(attributes):
                    sample.addExtension(extension)
            ids = biosample.find('Ids')
            extension = NCBIIDExtension('urn:mpeg:mpeg-g:metadata:extension:ncbi',self.extensionPath,'IDExtension')
            if extension.parse(ids):
                sample.addExtension(extension)
                samples.append(sample)
        return samples
