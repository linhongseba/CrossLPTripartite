Y=[0.5 0.5;0.5 0.5;1 0;0.5 0.5;0.5 0.5;0.5 0.5;0.5 0.5;0 1];

G=[0 0 1 1 0 0 1 0;
   0 0 0 1 1 1 1 1;
   1 0 0 0 0 0 1 0;
   1 1 0 0 0 0 1 0;
   0 1 0 0 0 0 1 1;
   0 1 0 0 0 0 1 1;
   1 1 1 1 1 1 0 0;
   0 1 0 0 1 1 0 0];

Sa=[0 0 0 0 0 0 0 0;
    0 0 0 0 0 0 0 0];
Sb=[0 0 1 0 0 0 0 0;
    0 0 0 0 0 0 0 0;
    0 0 0 0 0 0 0 0;
    0 0 0 0 0 0 0 0];
Sc=[0 0 0 0 0 0 0 0;
    0 0 0 0 0 0 0 1];
S=[0 0 0 0 0 0 0 0;
   0 0 0 0 0 0 0 0;
   0 0 1 0 0 0 0 0;
   0 0 0 0 0 0 0 0;
   0 0 0 0 0 0 0 0;
   0 0 0 0 0 0 0 0;
   0 0 0 0 0 0 0 0;
   0 0 0 0 0 0 0 1];

Gab=[1 1 0 0;
     0 1 1 1];
Gbc=[1 0;
     1 0;
     1 1;
     1 1];
Gac=[1 0;
     1 1];


Y0=Y

for i=1:10
	Ya=Y(1:2,:);
	Yb=Y(3:6,:);
	Yc=Y(7:8,:);
	Ya_new=Ya.*(sqrt((Gab*Yb+Gac*Yc+Sa*Y0)./(Ya*Yb'*Yb+Ya*Yc'*Yc+Sa*Y)));	
	Yb_new=Yb.*(sqrt((Gab'*Ya+Gbc*Yc+Sb*Y0)./(Yb*Ya'*Ya+Yb*Yc'*Yc+Sb*Y)));
	Yc_new=Yc.*(sqrt((Gac'*Ya+Gbc'*Yb+Sc*Y0)./(Yc*Yb'*Yb+Yc*Ya'*Ya+Sc*Y)));
	Y(1:2,:)=Ya_new;
	Y(3:6,:)=Yb_new;
	Y(7:8,:)=Yc_new;
	
	for j=1:8
  		Y(j,:)=Y(j,:)./sum(Y(j,:));
    end
    
    lambda=1;
    objective=2*(norm(Gab-Ya*Yb','fro')*norm(Gab-Ya*Yb','fro')+norm(Gac-Ya*Yc','fro')*norm(Gac-Ya*Yc','fro')+norm(Gbc-Yb*Yc','fro')*norm(Gbc-Yb*Yc','fro'))+lambda*norm(S*Y-S*Y0,'fro')*norm(S*Y-S*Y0,'fro')
    	Y
end
