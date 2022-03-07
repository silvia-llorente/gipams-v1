import sys
import os
from classes.organizations.NCBI import NCBI
from classes.organizations.ENA import ENA
from classes.DatasetGroup import DatasetGroup
import setup

def main():
    organization = input('NCBI o ENA?: ')
    if organization == 'NCBI':
        bioProjectPath = input('Path BioProject: ')
        bioSamplePath = input('Path BioSample: ')
        dg = NCBI(setup.MPEG_G_dgmd_path,setup.MPEG_G_dtmd_path,setup.NCBI_extensions_path).parse(bioProjectPath,bioSamplePath)
    elif organization == 'ENA':
        studyPath = input('Path Study: ')
        assemblyPath = input('Path Assembly: ')
        dg = ENA(setup.MPEG_G_dgmd_path,setup.MPEG_G_dtmd_path,setup.ENA_extensions_path).parse(studyPath,assemblyPath)
    else:
        print('Selecciona una organitació vàlida')
        sys.exit()
    outPath = input('Path fitxer de sortida: ')
    os.mkdir(outPath)
    dg.write(outPath)

main()