package org.fagu.fmv.core.utils;

/*
 * #%L
 * fmv-core
 * %%
 * Copyright (C) 2014 - 2015 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterComplexBase;
import org.fagu.fmv.ffmpeg.filter.FilterComplexBase.In;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.filter.IOKey;
import org.fagu.fmv.ffmpeg.filter.Label;
import org.fagu.fmv.ffmpeg.filter.OutputKey;
import org.fagu.fmv.ffmpeg.filter.graph.FilterGraph;
import org.fagu.fmv.ffmpeg.filter.graph.Visitor;
import org.fagu.fmv.ffmpeg.operation.AutoMap;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;


/**
 * @author f.agu
 *
 */
public class FilterGraphUI implements Visitor<Object> {

	private Graph graph;

	private Operation<?, ?> operation;

	private FilterNaming filterNaming;

	private FilterGraph filterGraph;

	/**
	 * @param builder
	 * @param filterGraph
	 */
	public FilterGraphUI(Operation<?, ?> operation, FilterGraph filterGraph) {
		this.operation = Objects.requireNonNull(operation);
		this.filterGraph = filterGraph;
		this.filterNaming = operation.getFilterNaming();
		graph = new MultiGraph("root");

		StringBuilder styelSheetBuf = new StringBuilder(100);
		styelSheetBuf.append("node {fill-color: black;}");
		styelSheetBuf.append("node.video {fill-color: red;}");
		styelSheetBuf.append("node.audio {fill-color: blue;}");
		styelSheetBuf.append("node.root {fill-color: green;}");

		graph.addAttribute("ui.stylesheet", styelSheetBuf.toString());

		addRoot();
	}

	/**
	 * @param builder
	 */
	public static void show(Operation<?, ?> operation) {
		FilterGraph filterGraph = operation.getFilterGraph();
		FilterGraphUI graphTest = new FilterGraphUI(operation, filterGraph);
		filterGraph.discover(() -> graphTest);
		graphTest.display();
	}

	/**
	 * @see org.fagu.fmv.ffmpeg.filter.graph.Visitor#visit(org.fagu.fmv.ffmpeg.filter.Label, java.util.Set,
	 *      org.fagu.fmv.ffmpeg.filter.FilterComplex, java.util.Set, org.fagu.fmv.ffmpeg.filter.Label, int)
	 */
	@Override
	public void visit(Label inLabel, Set<FilterComplex> inFilters, FilterComplex filterComplex, Set<FilterComplex> outFilters, Label outLabel,
			int depth) {

		Node node = createOrGetNode(filterComplex);

		// me -> input
		if(inLabel != null && inFilters != null) {
			for(FilterComplex fc : inFilters) {
				createOrGetEdge(inLabel, fc, node);
			}
		}

		// me -> output
		for(OutputKey outputKey : filterComplex.getOutputKeys()) {
			for(FilterComplex fc : filterGraph.getByIn(outputKey.getLabel())) {
				createOrGetEdge(outputKey.getLabel(), fc, node);
			}
		}
	}

	/**
	 *
	 */
	public void display() {
		addTail();
		Viewer viewer = graph.display();
	}

	// **************************************************

	/**
	 *
	 */
	private void addRoot() {
		Map<InputProcessor, Node> inputProcessorNodeMap = new HashMap<>();
		operation.getInputProcessorStream().forEach(ip -> {
			for(OutputKey outputKey : ip.getOutputKeys()) {
				Node node = createRootNode(ip, outputKey);
				inputProcessorNodeMap.put(ip, node);
			}
		});

		for(FilterComplex rootFC : filterGraph.getRoots()) {
			Map<IOKey, In> inputMap = rootFC.getInputMap();
			if( ! inputMap.isEmpty()) {
				for(Entry<IOKey, In> entry : inputMap.entrySet()) {
					FilterInput filterInput = entry.getValue().getFilterInput();
					if(filterInput instanceof InputProcessor) {
						Node node = inputProcessorNodeMap.get(filterInput);
						Edge edge = graph.addEdge(node.getId() + "-" + rootFC.toString(), createOrGetNode(rootFC), node);
						// edge.addAttribute("ui.label", values);
					}
				}
			}
		}
	}

	/**
	 *
	 */
	private void addTail() {
		// TODO autoMap by outputProcessor
		AutoMap autoMap = operation.getAutoMap();
		Set<Label> labels = autoMap.find(operation);

		Node node = graph.addNode("output");
		node.addAttribute("ui.label", "output");
		node.addAttribute("ui.class", "root");

		for(Label label : labels) {
			// System.out.println(filterNaming.generate(label));
			Set<FilterComplex> fcs = filterGraph.getByOut(label);
			for(FilterComplex outFC : fcs) {
				Node inNode = createOrGetNode(outFC);
				graph.addEdge("output" + label, inNode, node);
			}
		}
	}

	/**
	 * @param inputProcessor
	 * @param outputKey
	 * @return
	 */
	private Node createRootNode(InputProcessor inputProcessor, OutputKey outputKey) {
		Node node = graph.addNode(filterNaming.generate(outputKey.getLabel()));
		node.addAttribute("ui.label", inputProcessor.getInput().toString());
		node.addAttribute("ui.class", "root");
		return node;
	}

	/**
	 * @param label
	 * @param labeledFC
	 * @param node
	 * @return
	 */
	private Edge createOrGetEdge(Label label, FilterComplex labeledFC, Node node) {
		String edgeKey = getEdgeKey(label, labeledFC);
		Edge edge = graph.getEdge(edgeKey);
		if(edge == null) {
			Node inNode = createOrGetNode(labeledFC);
			edge = graph.addEdge(edgeKey, inNode, node);
			edge.addAttribute("ui.label", edgeKey);
		}
		return edge;
	}

	/**
	 * @param filterComplex
	 * @return
	 */
	private Node createOrGetNode(FilterComplex filterComplex) {
		String nodeKey = getNodeKey(filterComplex);
		Node node = graph.getNode(nodeKey);
		if(node == null) {
			node = graph.addNode(nodeKey);
			node.addAttribute("ui.label", nodeKey);
			Collection<Type> types = filterComplex.getTypes();
			String ccsStyle = null;
			if(types.contains(Type.AUDIO) && ! types.contains(Type.VIDEO)) {
				ccsStyle = "audio";
			} else if( ! types.contains(Type.AUDIO) && types.contains(Type.VIDEO)) {
				ccsStyle = "video";
			}
			if(ccsStyle != null) {
				node.setAttribute("ui.class", ccsStyle);
			}
		}
		return node;
	}

	// private Node createOrGet(FilterComplex filterComplex) {
	// String edgeKey = getEdgeKey(inLabel, fc);
	// Edge edge = graph.getEdge(edgeKey);
	// if(edge == null) {
	// Node inNode = createOrGet(fc);
	// edge = graph.addEdge(edgeKey, inNode, node);
	// edge.addAttribute("ui.label", edgeKey);
	// }
	// }

	/**
	 * @param filterComplex
	 * @return
	 */
	private String getNodeKey(FilterComplexBase filterComplex) {
		return filterComplex.toString();
	}

	/**
	 * @param label
	 * @return
	 */
	private String getEdgeKey(Label label, FilterComplex filterComplex) {
		StringBuilder buf = new StringBuilder();
		buf.append(filterNaming.generate(label)).append('-');
		for(Type type : filterComplex.getTypes()) {
			buf.append(Character.toUpperCase(type.code()));
		}
		return buf.toString();
	}
}
