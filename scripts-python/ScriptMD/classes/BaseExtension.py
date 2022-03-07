class BaseExtension:
    def __init__(self,URN,filename,name):
        self.urn = URN
        self.filename = filename
        self.name = name

    def write(self,xmlElem):
        return None
        
    def parse(self,xmlElem):
        return None