'''
Operators:
-arithmetical 2/1
-for 2
-if 3 (condition + 2 solution)
-functions n
-return 1
'''
from functools import partial

res=0
for c in range(0,10):
    res+=c 

class Tree:
    def __init__(self,type=None,operation=None,name=None,n_param=None,value=None,init=None,*args):
        if init is not None:
            init()
        if n_param is not None:
            self.n_param=n_param
        self.value=value
        self.operation=operation
        self.name=name
        self.args=[]
        self.type=type
        self.children=[]
        for a in args:
            self.args.append(a)

    def compute_arithmetic(self,scope):
        if self.operation=="+":
            res=0
            for c in self.children:
                res+=c.compute(scope)
            return res
        if self.operation=="*":
            res=1
            for c in self.children:
                res*=c.compute(scope)
            return res
        if self.operation=="-":
            res=0
            for c in self.children:
                res-=c.compute(scope)
            return res
        if self.operation=="/":
            res=1
            for c in self.children:
                res/=c.compute(scope)
            return res

    def compute(self,scope={}):
        if hasattr(self,"prec"):
            self.prec.compute(scope)

        if self.operation is None:
            for c in self.children:
                c.compute(scope)
            if self.value is not None:
                if callable(self.value):
                    return self.value(scope)
                else: 
                    return self.value

        elif self.operation=="function":
            list_param=[c.compute(scope) for c in self.children]
            self.value(*list_param)      

        elif self.operation in ["+","-","/","*"]:
            return self.compute_arithmetic(scope)
        
        elif self.operation=="assignment":
            scope[self.name]=self.children[0].compute(scope)

        elif self.operation=="for":
            if self.children[0].type==list:
                raise Exception('Type mismatch')
            range=self.children[0].compute(scope)
            first_time=True
            for c in range:
                if not first_time:
                    scope["range"].pop()
                if "range" in scope:
                    scope["range"].append(c)
                else:
                    scope["range"]=[c]
                self.children[1].compute(scope)
                first_time=False

        elif self.operation=="if":
            #operand 1 condition
            if self.children[0].compute(scope):
                #operand 2 for condition satisfied
                self.children[1].compute(scope)
            else:
                #operand 3 for false condition
                self.children[2].compute(scope)




    def add_child(self,child,order):
        child.father=self
        if order==0:
            self.prec=child
            return
        if len(self.children)<order:
            for a in range(0,order-len(self.children)):
                self.children.append(None)
        self.children[order-1]=child
'''
t=Tree(int,operation="+")
t.add_child(Tree(int,t,value=lambda :2),1)
t.add_child(Tree(int,t,value=lambda :6),2)

t=Tree(None,operation="for")
t.add_child(Tree(None,t,value=partial(assignment,"res",0)) ,3)
t.add_child(Tree(range,t,value=lambda _:[1,2,3]),1)
t.add_child(Tree(None,t,value=lambda x:print(x["range"])),2)
'''

'''
POSSIBLE OPERATIONS:
    -function: 1 or more children(Parameters)   Tree(operation="function",n_param=1,value=print)    
    -assignment: 1 children(value)              Tree(value=partial(assignment,"res",0))
    -math operations                            Tree(int,operation="+")
    -for: 2 children(1: range, 2: operations)   Tree(t,operation="for")
    -if
    -while


IF NONE:
    -value
    -
'''
def assignment(name,value,x):
    x[name]=value


t=Tree(operation="function",n_param=1,value=print)
t.add_child(Tree(int,value=lambda x:x["res"]),1)
nfor=Tree(t,operation="for")
t.add_child(nfor,0)

assign=Tree(operation="assignment",name="res")
nfor.add_child(Tree(range,value=[1,2,3]),1)
nfor.add_child(Tree(value=partial(assignment,"res",0)),0)
nfor.add_child(assign,2)

sumop=Tree(int,operation="+")
sumop.add_child(Tree(int,value=lambda x:x["res"]),1)
sumop.add_child(Tree(int,value=lambda x:x["range"][-1]),2)
assign.add_child(sumop,1)

t.compute()