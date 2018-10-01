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
    def __init__(self,type,father=None,operation=None,name=None,n_param=None,value=None,init=None,*args):
        if father is not None:
            self.father=father
        else:
            self.is_root=True
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

    def compute(self,scope):
        if self.operation is None:
            for c in self.children:
                c.compute(scope)
            if self.value is not None:
                if callable(self.value):
                    return self.value(scope)
                else: 
                    return self.value

        if self.operation=="function":
            if self.n_param<len(self.children):
                self.children[-1].compute(scope)
            list_param=[c.compute(scope) for c in self.children[:-1]]
            self.value(*list_param)
            
                

        if self.operation in ["+","-","/","*"]:
            return self.compute_arithmetic(scope)
        
        if self.operation=="assignment":
            scope[self.name]=self.children[0].compute(scope)


        if self.operation=="for":
            self.children[2].compute(scope)
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




    def add_child(self,child,order):
        child.father=self
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
def assignment(name,value,x):
    x[name]=value


t=Tree(None,operation="function",n_param=1,value=print)
t.add_child(Tree(int,t,value=lambda x:x["res"]),1)
nfor=Tree(t,operation="for")
t.add_child(nfor,2)
nfor.add_child(Tree(range,t,value=lambda _:[1,2,3]),1)
nfor.add_child(Tree(None,nfor,value=partial(assignment,"res",0)),3)
assign=Tree(nfor,nfor,operation="assignment",name="res")

sumop=Tree(int,operation="+")
sumop.add_child(Tree(int,value=lambda x:x["res"]),1)
sumop.add_child(Tree(int,value=lambda x:x["range"][-1]),2)
nfor.add_child(assign,2)
assign.add_child(sumop,1)

t.compute({})