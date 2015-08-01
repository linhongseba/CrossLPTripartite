>> Y=[0.5 0.5;0.5 0.5;1 0;0.5 0.5;0.5 0.5;0.5 0.5;0.5 0.5;0 1];

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
B=[0 0 1 0 1 0;
   0 0 0 1 0 1;
   1 0 0 0 1 0;
   0 1 0 0 0 1;
   1 0 1 0 0 0;
   0 1 0 1 0 0]

Bab=[1 0;
     0 1]
 
Bbc=[1 0;
     0 1]
 
Bac=[1 0;
     0 1]
 

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
    Bab_new=Bab.*(sqrt((Ya'*Gab*Yb)./(Ya'*Ya*Bab*Yb'*Yb)));
    Bbc_new=Bbc.*(sqrt((Yb'*Gbc*Yc)./(Yb'*Yb*Bbc*Yc'*Yc)));
    Bac_new=Bac.*(sqrt((Ya'*Gac*Yc)./(Ya'*Ya*Bac*Yc'*Yc)));
    Bab=Bab_new;
    Bbc=Bbc_new;
    Bac=Bac_new;
    %update Btt'
    
	
    Ya_new=Ya.*(sqrt((Gab*Yb*Bab'+Gac*Yc*Bac'+Sa*Y0)./(Ya*Bab*Yb'*Yb*Bab'+Ya*Bac*Yc'*Yc*Bac'+Sa*Y)));	
	Yb_new=Yb.*(sqrt((Gab'*Ya*Bab+Gbc*Yc*Bbc'+Sb*Y0)./(Yb*Bab'*Ya'*Ya*Bab+Yb*Bbc*Yc'*Yc*Bbc'+Sb*Y)));
	Yc_new=Yc.*(sqrt((Gac'*Ya*Bac+Gbc'*Yb*Bbc+Sc*Y0)./(Yc*Bbc'*Yb'*Yb*Bbc+Yc*Bac'*Ya'*Ya*Bac+Sc*Y)));
	Y(1:2,:)=Ya_new;
	Y(3:6,:)=Yb_new;
	Y(7:8,:)=Yc_new;
    %update Ytt'
	
	for j=1:8
  		Y(j,:)=Y(j,:)./sum(Y(j,:));
    end
    
    lambda=1;
    objective=2*(norm(Gab-Ya*Yb','fro')*norm(Gab-Ya*Yb','fro')+norm(Gac-Ya*Yc','fro')*norm(Gac-Ya*Yc','fro')+norm(Gbc-Yb*Yc','fro')*norm(Gbc-Yb*Yc','fro'))+lambda*norm(S*Y-S*Y0,'fro')*norm(S*Y-S*Y0,'fro')
    	Y
end