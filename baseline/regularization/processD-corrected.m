%clear all;

% load corpus;
% load indicator_text;
% load indicator_word;
% load indicatorMatrix_word;
% load allD;

% for i = 1:2000
%     U0(i,:) = [0.1 0.1];
% end
% 
% for i = 25000:27000
%     U0(i,:) = [0.1 0.1]; 
% end
clear all;

load ./Data/online/37/tweet-word-17.txt;
X=spconvert(tweet_word_17);
r = 2; %rank of H
[n,m] = size(X);
clearvars tweet_word_17;
U0=zeros(n,r);
for i = 1:n
    thres1=X(i,1)+X(i,3);
    thres2=X(i,2)+X(i,4);
    if thres1>thres2&&thres1>0
        U0(i,1) = 1;
    else
        if thres1<thres2&&thres2>0
            U0(i,2)=1;
        else
            U0(i,1) = 0.5;
            U0(i,2) = 0.5;
        end
    end
end
load ./Data/37/Sf0.txt
V0 = spconvert(Sf0);
clearvars Sf0;
X = sparse(X);
U0 = sparse(U0);
V0 = sparse(V0);
tlabel=zeros(n,1);
for i=1:size(tlabel)
    tlabel(i)=-1;
end;
load ./Data/online/37/tweetlabel-17.txt
v=tweetlabel_17(:,2);
index=tweetlabel_17(:,1);
for i=1:size(index)
    tlabel(index(i))=v(i);
end;
clearvars tweetlabel_17;
clear v index;
%set the parameters
alpha = 0.01;
beta = 0.01;
gamma = 0.01;
lambda = 0.01;
%make sure the elements in X is nonnegative and X is orthogonal
if min(min(X)) < 0
    error('The entries cannot be negative');
end
if min(sum(X,2)) == 0
    error('Summation of all entries in a row cannot be zero');
end
niter = 50;
verb = 0;
myeps = 1e-30;
norm_u = 0;
norm_v = 0;
orth_u = 1;
orth_v = 1;
accyp = zeros(niter,1);  %tweet-level accuracy of each iteration
MIp = zeros(niter,1);    %tweet-level MI of each iteration
U = [];
V = [];
if isempty(U)
    if isempty(U0)
        U = rand(n,r);
    else
        U = U0;
    end
    update_U = true;
else 
    update_U = false;
end

if isempty(V)
    if isempty(V0)
        V = rand(m,r);
    else
        V = V0;
    end
    update_V = true;
else % we aren't H
    update_V = false;
end

update_H = true;
H = rand(r,r);

% if ~(norm_w && orth_w) && ~(norm_h && orth_h)
%     warning('nmf_euc_orth: orthogonality constraints should be used with normalization on the same mode!');
% end
% 
% if norm_w ~= 0
%     % normalize W
%     W = normalize_W(W,norm_w);
% end
% 
% if norm_h ~= 0
%     % normalize H
%     H = normalize_H(H,norm_h);
% end

I = eye(r);
errs = zeros(niter,1);
option.Metric = 'Euclidean';
option.NeighborMode = 'KNN';
option.k = 4;
option.WeightMode = 'Binary';
option.t = 1;%this parameter is needed
%construct data graph
Wv = constructW(X',option);
Dv = diag(sum(Wv));
Lv = Dv - Wv;

%construct feature graph
Wu = constructW(X,option);
Du = diag(sum(Wu));
Lu = Du - Wu;

for t = 1:niter
    disp(t);
    if update_H         
        H = H .* sqrt(((U' * X * V) ./ max(U' * U * H * V' * V, myeps)));
    end
    TaoU = U' * X * V * H' - H*V'*V*H' - alpha * U'  * (U- U0) - gamma * U'*(Du - Wu)*U;
    %TaoU = U' * X * V * H' - H*V'*V*H';
    TaoUplus = (abs(TaoU) + TaoU)./2;
    TaoUminus = (abs(TaoU) - TaoU)./2;
    
    if update_U
        U = U .* sqrt((X * V * H' + alpha*U0 + gamma * Wu * U0 + U* TaoUminus) ./ max((U * H * V' * V * H' + alpha  * U + gamma * Du * U + U * TaoUplus), myeps));
    end
    
    TaoV = V' * X' * U * H - H' * U' * U * H - lambda * V'  *  V + lambda *  V'  * V0 - beta * V' * (Dv - Wv) * V;
    %TaoV = V' * X' * U * H - H' * U' * U * H;
    TaoVplus = (abs(TaoV) + TaoV)./2;
    TaoVminus = (abs(TaoV) - TaoV)./2;
    
    if update_V
        V = V .* sqrt((X' * U * H + lambda  * V0 + beta * Wv * V + V * TaoVminus) ./ max((V * H' * U' * U * H + lambda  * V + V* TaoVplus + beta * Dv * V), myeps));
    end
    errs(t) = sum(sum((X-U*H*V').^2));
    disp(errs(t));
    %tweet-level NMI
        res = zeros(n,1);
        for j = 1:n
            [~, res(j)] = max(U(j,:));
        end
        MIp(t) = MutualInfo(tlabel,res)*100;
	%tweet-level accuracy
	res = bestMap(tlabel,res);
    accyp(t) = length(find(tlabel == res))/length(tlabel)*100;
end






