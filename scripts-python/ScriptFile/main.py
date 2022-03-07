from classes.MPEG_G import MPEG_G
from classes.Dataset import Dataset
import os

paths = ["files/ENA_out2"]
datasetNum = [2]

def main():
    version = '2000'
    MPEG_file = MPEG_G()
    MPEG_file.makeFileHeader('MPEG-G',version,[])
    i = 0
    for path in paths:
        metadata = None
        protection = None

        with open(path+"/dgmt.xml","r") as f: 
            metadata = f.read()

        with open(path+"/dgpr.xml","r") as f:
            protection = f.read()

        MPEG_file.addDatasetGroup(0,metadata,protection)
        for j in range(datasetNum[i]):

            metadata = None
            protection = None

            try:
                with open(path+"/dtmt_{}.xml".format(j),"r") as f: 
                    metadata = f.read()
            except:
                pass

            try:
                with open(path+"/dtpr_{}.xml".format(j),"r") as f:
                    protection = f.read()
            except:
                pass

            dt = MPEG_file.createDataset()
            dt.addHeader(version,0,0)
            if (metadata != None):
                dt.addMetadata(metadata)
            if (protection != None):
                dt.addProtection(protection)

            MPEG_file.addDataset(i,dt)
            for k in range(3):
                MPEG_file.addAccessUnit(i,j,0,1,0,'AU_information_value','AU_protection_value')
                for l in range(5):
                    MPEG_file.addBlock(i,j,k,os.urandom(20))
        i += 1

    MPEG_file.write('filesOut/MPEG-G_sample_2_18-04-21')
main()