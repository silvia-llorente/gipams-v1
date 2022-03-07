class Sample:
    def __init__(self):
        self.TaxonId = None
        self.Title = None
        self.extensions = None
        
    def addExtension(self,extension):
        if self.extensions == None:
            self.extensions = [extension]
        else:
            self.extensions.append(extension)