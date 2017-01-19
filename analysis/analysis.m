% close all;
clear;

% load(['/home/sss1/Desktop/SORN/resultsMemory1.mat'])
% weights = reshape(weights, weights_dims);
% weights = weights(3:(duration - 2), :, :);
% fired = fired(3:(duration - 2), :);

mems = [0:4 10];
path_lengths = [2];
figure; hold all;
for mem_idx = 1:length(mems)
  mem = mems(mem_idx);
  load(['/home/sss1/Desktop/SORN/resultsMemory' num2str(mem) '.mat'])

  weights = reshape(weights, weights_dims);
  
  traces = zeros(duration, 1);
  for t = 1:duration
    w = squeeze(weights(t, :, :));
    traces(t) = trace(w * w * w * w);
  end
  % A = squeeze(weights(end, :, :) > 0);
  % for path_length_idx = 1:length(path_lengths)
  %   path_length = path_lengths(path_length_idx);
  %   traces(mem, path_length) = trace(A^path_length);
  % end
  plot(smooth(traces, 20));
end
