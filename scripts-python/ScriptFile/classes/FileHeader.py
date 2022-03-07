from classes.KLV import KLV

class FileHeader:
    def __init__(self,major_brand,minor_version,compatibleBrand):
        self.major_brand = major_brand
        self.minor_version = minor_version
        self.compatibleBrand = compatibleBrand
        if len(major_brand) != 6:
            raise ValueError('Invalid major_brand')
        elif len(minor_version) != 4:
            raise ValueError('Invalid minor_version')
        for brand in self.compatibleBrand:
            if len(brand) != 4:
                raise ValueError('Invalid brand')

    def write(self,path):
        value = bytes(self.major_brand,'utf-8')+bytes(self.minor_version,'utf-8')
        for brand in self.compatibleBrand:
            value += bytes(brand,'utf-8')
        klv = KLV('flhd',value)
        klv.write(path)