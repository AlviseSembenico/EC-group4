import inspect

def is_number(n):
    try:
        float(n)
        return True
    except:
        return False

class math_op:

    def __init__(self,operation,right,left,chain=None):
        self.operation=operation
        self.right=right
        self.left=left
        self.chain=chain

    def compute_operation(self,l,r):
        if self.operation=="+":
            return l+r
        elif self.operation=="*":
            return l*r
        elif self.operation=="-":
            return l-r
        elif self.operation=="/":
            return l/r

    def compute(self,context={}):
        if self.chain is not None:
            self.chain.compute(context)

        if is_number(self.right):
            r=self.right
        else:
            r=self.right.compute(context)

        if is_number(self.left):
            l=self.left
        else:
            l=self.left.compute(context)

        return self.compute_operation(l,r)

class assignment_op:

    def __init__(self,var_name,value,chain=None):
        self.var_name=var_name
        self.value=value
        self.chain=chain

    def compute(self,context={}):
        if self.chain is not None:
            self.chain.compute(context)
        if inspect.isclass(self.value):
            v=self.value.compute(context)
        else:
            v=self.value
        context[self.var_name]=v

class for_op:
    def __init__(self,iterator,child,chain=None):
        self.iterator=iterator
        self.child=child
        self.chain=chain

    def compute(self,context={}):
        if self.chain is not None:
            self.chain.compute(context)
        if inspect.isclass(self.iterator):
            iter=self.iterator.compute(context)
        else:
            iter=self.iterator
        if not 'range' in context:
            context['range']=[]
        for c in iter:
            context['range'].append(c)
            self.child.compute(context)

class if_op:
    def __init__(self,condition,tchild,fchild=None,chain=None):
        self.condition=condition
        self.tchild=tchild
        self.fchild=fchild
        self.chain=chain

    def compute(self,context={}):
        if self.chain is not None:
            self.chain.compute(context)

        if inspect.isclass(self.condition):
            cond=self.condition.compute(context)
        
        if cond:
            self.tchild.compute(context)
        else:
            if self.fchild is not None:
                self.fchild.compute(context)

class function:   
    def __init__(self,function,arguments=None,chain=None):
        self.function=function
        self.arguments=arguments
        self.chain=chain

    def compute(self,context={}):
        if self.chain is not None:
            self.chain.compute(context)
        if self.arguments is None:
            return self.function()
        
        arg=[]
        for argm in self.arguments:
            if inspect.isclass(argm):
                arg.append(argm.compute(context))
            else:
                arg.append(argm)
        
        return self.function(*arg)