Y=[0.5 0.5;0.5 0.5;1 0;0.5 0.5;0.5 0.5;0.5 0.5;0.5 0.5;0 1];

G=[0 0 1 1 0 0 1 0;
   0 0 0 1 1 1 1 1;
   1 0 0 0 0 0 1 0;
   1 1 0 0 0 0 1 0;
   0 1 0 0 0 0 1 1;
   0 1 0 0 0 0 1 1;
   1 1 1 1 1 1 0 0;
   0 1 0 0 1 1 0 0];

T=[0 0 1 1 1 1 1 1;
   0 0 1 1 1 1 1 1;
   1 1 0 0 0 0 1 1;
   1 1 0 0 0 0 1 1;
   1 1 0 0 0 0 1 1;
   1 1 0 0 0 0 1 1;
   1 1 1 1 1 1 0 0;
   1 1 1 1 1 1 0 0];

Y0=Y
I=[1 0;
   0 1];

for i=1:10
    
    Ynew=zeros(8,2);
	for u=1:8
        A=zeros(2,2);
		for(v=1:8)
			if(T(u,v)==1)
				A=A+Y(v,:)'*Y(v,:);
			end
		end
		temp=zeros(2,1);
		for(v=1:8)
			if(G(u,v)==1)
				temp=temp+Y(v,:)';
			end
		end
		
		
		if(u==3||u==8)
			A=A+I;
			temp=temp+Y0(u,:)';
        end
	    Ynew(u,:)=inv(A)*det(A)*temp;
		Ynew(u,:)=1/sum(Ynew(u,:)).*Ynew(u,:);
    end
    Y=Ynew
    objective=0.0;
    for u=1:8
        for v=1:8
            if(G(u,v)==1)
               	 objective=objective+(G(u,v)-Y(u,:)*Y(v,:)')*(G(u,v)-Y(u,:)*Y(v,:)');
            end
            if(G(u,v)==0&&T(u,v)==1)
               	 objective=objective+(Y(u,:)*Y(v,:)')*(Y(u,:)*Y(v,:)');
            end
        end
        if(u==3||u==8)
            objective=objective+norm(Y(u,:)-Y0(u,:),'fro')*norm(Y(u,:)-Y0(u,:),'fro');
        end
    end
    objective
	Y
end